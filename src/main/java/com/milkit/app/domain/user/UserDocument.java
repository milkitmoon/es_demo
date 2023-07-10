package com.milkit.app.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "users")
public class UserDocument {

	@Id
    private Long id;

    @Field(type = FieldType.Keyword)
    private String userId;

    @Field(type = FieldType.Keyword)
    private String role;

    @Field(type = FieldType.Keyword)
    private String useYn;

    @Field(type = FieldType.Text)
	private String description;

    @Field(type = FieldType.Date, format = {DateFormat.date_hour_minute_second_millis, DateFormat.epoch_millis})
    private LocalDateTime instTime;

    @Field(type = FieldType.Date, format = {DateFormat.date_hour_minute_second_millis, DateFormat.epoch_millis})
    private LocalDateTime updTime;

    @Field(type = FieldType.Keyword)
    private String instUser;

    @Field(type = FieldType.Keyword)
	private String updUser;

    public UserDocument(String userId) {
        this.userId = userId;
        this.role = RoleEnum.MEMBER.getValue();
    }

    public static UserDocument from(User user) {
        return UserDocument.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .role(user.getRole())
                .useYn(user.getUseYn())
                .description(user.getDescription())
                .instTime(user.getInstTime())
                .updTime(user.getUpdTime())
                .instUser(user.getInstUser())
                .updUser(user.getUpdUser())
                .build();
    }
		
	@Override  
	public String toString() {
		return ToStringBuilder.reflectionToString(
				this, ToStringStyle.SHORT_PREFIX_STYLE
		);
	}
}
