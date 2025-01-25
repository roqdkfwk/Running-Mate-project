package com.suseok.run.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.suseok.run.jwtutill.AuthRequired;
import com.suseok.run.model.dto.User;
import com.suseok.run.model.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/users")
@Tag(name = "UserRestController", description = "유저CRUD")
@RequiredArgsConstructor
public class UserController {

	// TODO Controller와 Service 로직 분리
	private final UserService userService;

	@PostMapping("/signup")
	@Operation(summary = "signup")
	public ResponseEntity<Boolean> signup(
			@RequestBody User user
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.signup(user));
	}
	
	@GetMapping
	@Operation(summary = "selectAllUsers")
	public ResponseEntity<List<User>> selectAllUsers() {
		return new ResponseEntity<List<User>>(userService.selectAll(),HttpStatus.OK);
	}

	@GetMapping("/check-id/{userId}")
	@Operation(summary = "아이디 중복확인")
	public ResponseEntity<Boolean> checkId(
			@PathVariable String userId
	) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.checkId(userId));
	}
	
	@GetMapping("/signup/cn/{checkNick}")
	@Operation(summary = "checkNick")
	public ResponseEntity<?> checkNick(
			@PathVariable String checkNick
	) {
		if (userService.selectByNick(checkNick) != null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		else
			return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/findId")
    @Operation(summary = "findId")
    public ResponseEntity<?> findId(
			@RequestBody User user
	) {
        User foundUser = userService.findId(user.getUserName(), user.getPhone());
        if (foundUser == null) {
            foundUser = userService.findId(user.getUserName(), user.getEmail());
        }
        if (foundUser == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(foundUser.getUserId(), HttpStatus.OK);
    }

    @PostMapping("/findPwd")
    @Operation(summary = "findPwd")
    public ResponseEntity<?> findPwd(
			@RequestBody User user
	) {
        User foundUser = userService.findPwd(user.getUserName(), user.getPhone(), user.getUserId());
        if (foundUser == null) {
            foundUser = userService.findPwd(user.getUserName(), user.getEmail(), user.getUserId());
        }
        if (foundUser == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // 새로운 비밀번호 생성 
        String newPwd = userService.sendNewPassword(foundUser);
        return new ResponseEntity<>(newPwd, HttpStatus.OK);
    }
	
	@AuthRequired
	@DeleteMapping("/withdraw")
	@Operation(summary = "withdraw")
	public ResponseEntity<?> withdraw(
			@RequestHeader("userId") String userId
	) {
		if(userService.delete(userId))
			return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
		return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
	}
	
	@AuthRequired 
	@GetMapping("/myPage")

	@Operation(summary = "myPage", description = "유저 정보")
	public ResponseEntity<User> myPage(
			@RequestHeader("userId") String userId
	) {
		User user = userService.selectById(userId);
		if (user != null)
			return new ResponseEntity<User>(user, HttpStatus.OK);
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@AuthRequired 
	@PutMapping
	@Operation(summary = "updateMyPage")
	public ResponseEntity<?> updateMyPage(
			@RequestHeader("userId") String userId,
			@RequestBody User user
	) {
		if (userId != user.getUserId())
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		if (userService.update(user))
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
	}

	@AuthRequired 
	@GetMapping("/add/{rivalId}")
	@Operation(summary = "addRival")
	public ResponseEntity<?> addRival(
			@RequestHeader("userId") String userId,
			@PathVariable("rivalId") String rivalId
	) {
		if (userService.addRival(userId, rivalId))
			return new ResponseEntity<>(HttpStatus.OK);
		return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
	}

	@PostMapping("/search")
	@Operation(summary = "searchUser")
	public ResponseEntity<List<User>> searchUser(
			@RequestParam String con
	) {
		List<User> users = userService.search(con);
		if (users != null)
			return new ResponseEntity<List<User>>(users, HttpStatus.OK);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
