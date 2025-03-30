package com.peakmeshop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peakmeshop.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByMemberId(Long memberId);

    Optional<Address> findByIdAndMemberId(Long id, Long memberId);

    boolean existsByMemberId(Long memberId);

    Optional<Address> findFirstByMemberIdOrderByCreatedAtAsc(Long memberId);

    Optional<Address> findByMemberIdAndIsDefaultTrue(Long memberId);
}