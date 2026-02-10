package com.racha.api.usecase.expense;

import com.racha.api.domain.entity.ExpenseSplit;
import com.racha.api.domain.entity.GroupMember;
import com.racha.api.domain.entity.User;
import com.racha.api.domain.repository.ExpenseSplitRepository;
import com.racha.api.domain.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettleDebtsUseCase {

    private final GroupMemberRepository groupMemberRepository;
    private final ExpenseSplitRepository expenseSplitRepository;

    @Transactional
    public void execute(UUID groupId) {
        List<User> users = groupMemberRepository.findByGroupId(groupId).stream()
                .map(GroupMember::getUser)
                .collect(Collectors.toList());

        for (int i = 0; i < users.size(); i++) {
            for (int j = i + 1; j < users.size(); j++) {
                User userA = users.get(i);
                User userB = users.get(j);

                settleBetweenTwoUsers(groupId, userA, userB);
            }
        }
    }

    private void settleBetweenTwoUsers(UUID groupId, User userA, User userB) {
        List<ExpenseSplit> aOwesB = expenseSplitRepository.findUnpaidSplitsBetweenUsers(groupId, userA.getId(), userB.getId());
        BigDecimal totalAowesB = aOwesB.stream().map(ExpenseSplit::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ExpenseSplit> bOwesA = expenseSplitRepository.findUnpaidSplitsBetweenUsers(groupId, userB.getId(), userA.getId());
        BigDecimal totalBowesA = bOwesA.stream().map(ExpenseSplit::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal settlementAmount = totalAowesB.min(totalBowesA);

        if (settlementAmount.compareTo(BigDecimal.ZERO) > 0) {
            markSplitsAsPaidForAmount(aOwesB, settlementAmount);
            markSplitsAsPaidForAmount(bOwesA, settlementAmount);
        }
    }

    private void markSplitsAsPaidForAmount(List<ExpenseSplit> splits, BigDecimal amountToSettle) {
        BigDecimal settledAmount = BigDecimal.ZERO;
        List<ExpenseSplit> splitsToUpdate = new ArrayList<>();

        for (ExpenseSplit split : splits) {
            if (settledAmount.compareTo(amountToSettle) >= 0) {
                break;
            }

            BigDecimal remainingToSettle = amountToSettle.subtract(settledAmount);

            if (split.getAmount().compareTo(remainingToSettle) <= 0) {
                split.setPaid(true);
                split.setPaidAt(LocalDateTime.now());
                splitsToUpdate.add(split);
                settledAmount = settledAmount.add(split.getAmount());
            }
        }

        if (!splitsToUpdate.isEmpty()) {
            expenseSplitRepository.saveAll(splitsToUpdate);
        }
    }
}
