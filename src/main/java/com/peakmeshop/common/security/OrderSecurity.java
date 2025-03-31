package com.peakmeshop.common.security;

import java.security.Principal;

import org.springframework.stereotype.Component;

import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.domain.repository.OrderRepository;

@Component
public class OrderSecurity {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;

    public OrderSecurity(OrderRepository orderRepository, MemberRepository memberRepository) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * 현재 로그인한 사용자가 주문의 소유자인지 확인
     * @param orderId 주문 ID
     * @param principal 현재 로그인한 사용자
     * @return 소유자 여부
     */
    public boolean isOrderOwner(Long orderId, Principal principal) {
        if (principal == null) {
            return false;
        }

        return memberRepository.findByEmail(principal.getName())
                .flatMap(member -> orderRepository.findById(orderId)
                        .map(order -> order.getMember().getId().equals(member.getId())))
                .orElse(false);
    }
}