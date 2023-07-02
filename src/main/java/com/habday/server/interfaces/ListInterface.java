package com.habday.server.interfaces;

import com.habday.server.classes.implemented.HostedList;
import com.habday.server.domain.member.Member;
import org.springframework.data.domain.Pageable;

import java.util.List;

//HostedList/ParticipatedList
public interface ListInterface<T> {
    public List<T> getProgressList(Member member, Long pointId, Pageable page);

    public List<T> getFinishedList(Member member, Long pointId,  Pageable page);
}
