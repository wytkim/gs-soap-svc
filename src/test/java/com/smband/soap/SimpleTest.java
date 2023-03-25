package com.smband.soap;

import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

//import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleTest {

	@Test
	public void checkResourceBundle() {
		ResourceBundle bundle = ResourceBundle.getBundle("MessageBundle");
		bundle.keySet().stream().forEach(key -> {
			String value = bundle.getString(key);
			log.info("{}	: {}", key, value);
		});
	}
}
