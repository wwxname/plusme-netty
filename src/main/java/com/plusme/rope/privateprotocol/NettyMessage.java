package com.plusme.rope.privateprotocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @author plusme
 * @create 2019-12-01 20:49
 */
@Data
public final class NettyMessage implements Serializable {
    private Header header;
    private Object body;
}
