package com.plusme.rope.privateprotocol;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author plusme
 * @create 2019-12-01 20:50
 */
@Data
public class Header {
    private int crcCode = 0xabef0101;
    private int length;
    private long sessionID;
    private byte type;
    private byte priority;
    private Map<String, Object> attachment = new HashMap<>();
}
