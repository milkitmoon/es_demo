package com.milkit.app.common.pattern;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class PatternMatcherServiceImpl {

	private final static String regexKey = "\\#\\{([\\w|\\uac00-\\ud7a3]*)\\}"; // #{key}
	
	public static String getMatchingMessage(String originMessage, Map<String, String> symbol, String regexKey) {
		String matchingMessage = null;
		
		if(originMessage != null && symbol != null) {
			Pattern pattern = Pattern.compile(regexKey);
	
			Matcher matcher = pattern.matcher(originMessage);
			StringBuilder sb = new StringBuilder();
			while (matcher.find()) {
				String key = matcher.group(1);
				String replacementValue = symbol.get(key);
				matcher.appendReplacement(sb, Objects.requireNonNullElse(replacementValue, ""));
			}
			matcher.appendTail(sb);
			matchingMessage = sb.toString();
		}
		
		return matchingMessage;
	}

	public static String getMatchingMessage(String originMessage, Map<String, String> symbol) {
		return getMatchingMessage(originMessage, symbol, regexKey);
	}
	
	public static String getMatchingMessage(String originMessage, String[] symbol) {
		AtomicInteger atomicInteger = new AtomicInteger(0);
		Map<String, String> map = Arrays.stream(symbol)
		.collect(Collectors.toMap (x -> String.valueOf(atomicInteger.getAndIncrement()), Function.identity()));
		
		return getMatchingMessage(originMessage, map, regexKey);
	}

	public static boolean hasMatchingMessage(String originMessage) {
		return hasMatchingMessage(originMessage, regexKey);
	}
	
	public static boolean hasMatchingMessage(String originMessage, String regexKey) {
		Pattern pattern = Pattern.compile(regexKey);
		Matcher matcher = pattern.matcher(originMessage);

		return matcher.find();
	}
}
