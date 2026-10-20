package com.example.demo.dto;

import java.util.List;
import java.util.UUID;

public record CursorPageResponse<T>(
        List<T> content,
        UUID nextCursor
) {
}
