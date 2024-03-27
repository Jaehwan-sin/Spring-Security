package com.cos.security1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.security1.model.User;

// CRUD 함수를 JPARepository가 들고있음
// @Repository라는 어노테이션이 없어도 IoC된다. JpaRepository를 상속받고있어서
public interface UserRepository extends JpaRepository<User, Integer>{
	
	// findBy 까지는 규칙 -> Username은 문법
	// 실행하면 select * from user where username = ? 쿼리가 실행
	public User findByUsername(String username); // JPA Query Method
	
}
