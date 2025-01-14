package com.suseok.run.model.service;

import java.util.List;
import java.util.Random;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.suseok.run.model.dao.UserDao;
import com.suseok.run.model.dto.User;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserDao userDao;
	private final RedisTemplate<String, String> redisTemplate;

	@Override
	public User loginUser(User user) {
		return userDao.loginUser(user);
	}

	@Override
	public boolean signup(User user) {
		return userDao.signup(user);
	}

	@Override
	public User selectById(String userId) {
		return userDao.selectById(userId);
	}

	@Override
	public boolean update(User user) {
		return userDao.update(user);
	}

	@Override
	public boolean addRival(String userId, String rivalId) {
		int userSeq = userDao.selectById(userId).getUserSeq();
		int rivalSeq = userDao.selectById(rivalId).getUserSeq();
		return userDao.addRival(userSeq, rivalSeq);
	}

	@Override
	public List<User> search(String con) {
		return userDao.search(con);
	}

	@Override
	public boolean delete(String userId) {
		return userDao.delete(userId);
	}

	@Override
	public User findId(String name, String phoneOrEmail) {
		return userDao.findId(name, phoneOrEmail);
	}

	@Override
	public User findPwd(String name, String phoneOrEmail, String id) {
		return userDao.findPwd(name, phoneOrEmail, id);
	}

	@Override
	public String sendNewPassword(User user) {

		// 사용할 문자와 숫자 집합
		final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

		Random random = new Random();
		StringBuilder sb = new StringBuilder(10);

		for (int i = 0; i < 10; i++) {
			int index = random.nextInt(CHARACTERS.length());
			sb.append(CHARACTERS.charAt(index));
		}

		String randomString = sb.toString();
		user.setUserPwd(randomString);
		update(user);
		
		return randomString;
	}

	@Override
	public User selectByNick(String userNick) {
		return userDao.selectByNick(userNick);
	}

	@Override
	public List<User> selectAll() {
		return userDao.selectAll();
	}

	@Override
	public Integer findByEmail(String email) {
        return userDao.findByEmail(email);
    }
}
