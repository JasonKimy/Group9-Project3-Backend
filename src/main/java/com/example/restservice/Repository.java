package com.example.restservice;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeckRepository extends JpaRepository<Deck, String> {}
public interface UserRepository extends JpaRepository<User, String> {}
public interface CheckInRepository extends JpaRepository<CheckIn, String> {}
