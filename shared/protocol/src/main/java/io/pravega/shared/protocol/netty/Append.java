/**
 * Copyright (c) 2017 Dell Inc., or its subsidiaries. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package io.pravega.shared.protocol.netty;

import io.netty.buffer.ByteBuf;
import io.pravega.shared.protocol.netty.WireCommands.Event;
import java.util.UUID;
import lombok.Data;

@Data
public class Append implements Request, Comparable<Append> {
    final String segment;
    final UUID writerId;
    final long eventNumber;
    final int eventCount;
    final ByteBuf data;
    final Long expectedLength;
    final long flowId;
    final long beginTime;
    final long pendingEventTime;
    final long appendTime;
    final boolean print;

    public Append(String segment, UUID writerId, long eventNumber, Event event, long flowId) {
        this(segment, writerId, eventNumber, 1, event.getAsByteBuf(), null, flowId, 0, 0, false);
    }
    
    public Append(String segment, UUID writerId, long eventNumber, Event event, long expectedLength, long flowId) {
        this(segment, writerId, eventNumber, 1, event.getAsByteBuf(), expectedLength, flowId,0,0, false);
    }

    public Append(String segment, UUID writerId, long eventNumber, int eventCount, ByteBuf data, Long expectedLength, long flowId) {
        this(segment, writerId,eventNumber, eventCount, data, expectedLength, flowId, 0, 0, false);
    }

    public Append(String segment, UUID writerId, long eventNumber, Event event, long expectedLength, long flowId, long beginTime, long pendingEventTime) {
        this(segment, writerId, eventNumber, 1, event.getAsByteBuf(), expectedLength, flowId, beginTime, pendingEventTime, false);
    }

    public Append(boolean print){
        this(null, null, 0, 1, null, 0L, 0, 0, 0, true);
    }

    public Append(String segment, UUID writerId, long eventNumber, int eventCount, ByteBuf data, Long expectedLength,
                  long flowId, long beginTime, long pendingEventTime) {
        this(segment,writerId,eventNumber,eventCount,data,expectedLength,flowId,beginTime,pendingEventTime,false);
    }

    public Append(String segment, UUID writerId, long eventNumber, int eventCount, ByteBuf data, Long expectedLength,
                  long flowId, long beginTime, long pendingEventTime, boolean print) {
        this.segment = segment;
        this.writerId = writerId;
        this.eventNumber = eventNumber;
        this.eventCount = eventCount;
        this.data = data;
        this.expectedLength = expectedLength;
        this.flowId = flowId;
        this.beginTime = beginTime;
        this.pendingEventTime = pendingEventTime;
        this.appendTime = System.currentTimeMillis();
        this.print = print;
    }
    
    public int getDataLength() {
        return data.readableBytes();
    }

    public boolean isConditional() {
        return expectedLength != null;
    }

    @Override
    public void process(RequestProcessor cp) {
        cp.append(this);
    }

    @Override
    public int compareTo(Append other) {
        return Long.compare(eventNumber, other.eventNumber);
    }

    @Override
    public long getRequestId() {
        return flowId;
    }
}
