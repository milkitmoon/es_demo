package com.milkit.app.temp;

import java.util.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.milkit.app.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class DateUtilTest {

	@Test
	@DisplayName("1. 비교대상 시간의 차이를 비교한다.")
	public void compareDate_TEST() throws Exception {
		Date currDate = new Date();
		Date compareDate = DateUtil.plusMin(currDate, 1);

		int result = DateUtil.compareDate(currDate, compareDate);

		assertTrue(result > 0);
    }
	
	
	@Test
	@DisplayName("2. 시간정보 포맷 조회.")
	public void getFormattedTimeString_TEST() throws Exception {
		String result = DateUtil.getFormatedTimeString(1609320299982L, "yyyy-MM-dd HH:mm:ss");

		assertNotNull(result);
    }
}
