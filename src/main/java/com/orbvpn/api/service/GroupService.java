package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.GroupEdit;
import com.orbvpn.api.domain.dto.GroupView;
import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.ServiceGroup;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.mapper.GroupEditMapper;
import com.orbvpn.api.mapper.GroupViewMapper;
import com.orbvpn.api.reposiitory.GroupRepository;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

  private final GroupRepository groupRepository;
  private final GroupViewMapper groupViewMapper;
  private final GroupEditMapper groupEditMapper;
  private final ServiceGroupService serviceGroupService;

  @Transactional
  public GroupView createGroup(GroupEdit groupEdit) {
    Group group = groupEditMapper.create(groupEdit);

    groupRepository.save(group);

    return groupViewMapper.toView(group);
  }

  @Transactional
  public GroupView editGroup(int id, GroupEdit groupEdit) {
    Group group = getById(id);

    Group edited = groupEditMapper.edit(group, groupEdit);

    groupRepository.save(edited);

    return groupViewMapper.toView(edited);
  }

  @Transactional
  public GroupView deleteGroup(int id) {
    Group group = getById(id);

    groupRepository.delete(group);

    return groupViewMapper.toView(group);
  }

  @Transactional
  public List<GroupView> getGroups(int serviceGroupId) {
    ServiceGroup serviceGroup = serviceGroupService.getById(serviceGroupId);
    return groupRepository.findAllByServiceGroup(serviceGroup)
      .stream()
      .map(groupViewMapper::toView)
      .collect(Collectors.toList());
  }

  @Transactional
  public GroupView getGroup(int id) {
    Group group = getById(id);

    return groupViewMapper.toView(group);
  }

  public Group getById(int id) {
    return groupRepository.findById(id)
      .orElseThrow(()->new NotFoundException("Group not found"));
  }

}
