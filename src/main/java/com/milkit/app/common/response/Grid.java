package com.milkit.app.common.response;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Schema
public class Grid<T> {

	@Schema(description = "현재Page 번호", ref = "기본값 (1)")
	private String page;
	@Schema(description = "전체Page 갯수")
	private String total;
	@Schema(description = "전체 record 갯수")
	private String records;
	@Schema(description = "record 의 결과값", ref = "형식 (List)")
	protected List<T> rows;
	
	public Grid() {
		this.page = "1";
		this.total = "0";
		this.records = "0";
		this.rows = new ArrayList<T>();
	}
	
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	
	public void setTotal(int total) {
		this.total = Integer.toString(total);
	}
	public String getRecords() {
		return records;
	}
	public void setRecords(String records) {
		this.records = records;
	}
	public void setRecords(int records) {
		this.records = Integer.toString(records);
	}
	
	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
	
    @Override  
	public String toString() {
		return ToStringBuilder.reflectionToString(
				this, ToStringStyle.SHORT_PREFIX_STYLE
		);
    }
}
