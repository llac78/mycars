package com.project.mycars.rest.exception;

import lombok.Getter;

@Getter
public record ApiErrors (String message, int errorCode) {
}
