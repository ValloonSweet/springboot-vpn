package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.GroupEdit;
import com.orbvpn.api.domain.dto.GroupView;
import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.mapper.GroupViewMapper;
import com.orbvpn.api.reposiitory.GroupRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

  private final GroupRepository groupRepository;
  private final GroupViewMapper groupViewMapper;

  public GroupView createGroup(GroupEdit groupEdit) {
    Group group = new Group();

    group.setName(groupEdit.getName());

    groupRepository.save(group);

    return groupViewMapper.toView(group);
  }

  public List<GroupView> getGroups() {
    return groupRepository.findAll()
      .stream()
      .map(groupViewMapper::toView)
      .collect(Collectors.toList());
  }

}
