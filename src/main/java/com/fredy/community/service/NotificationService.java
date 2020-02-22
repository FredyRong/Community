package com.fredy.community.service;

import com.fredy.community.dto.NotificationDTO;
import com.fredy.community.dto.PaginationDTO;
import com.fredy.community.enums.NotificationStatusEnum;
import com.fredy.community.enums.NotificationTypeEnum;
import com.fredy.community.exception.CustomizeErrorCode;
import com.fredy.community.exception.CustomizeException;
import com.fredy.community.mapper.NotificationMapper;
import com.fredy.community.mapper.UserMapper;
import com.fredy.community.model.Notification;
import com.fredy.community.model.NotificationExample;
import com.fredy.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {
    @Autowired(required = false)
    private NotificationMapper notificationMapper;

    public PaginationDTO list(Long userId, Integer page, Integer size) {
        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(userId);
        Integer totalCount = (int) notificationMapper.countByExample(example);
        Integer totalPage = (int) Math.ceil(totalCount / 1.0 / size);

        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        if (totalCount != 0) {
            page = Math.max(1, page);
            page = Math.min(totalPage, page);
            Integer offset = size * (page - 1);

            NotificationExample notificationExample = new NotificationExample();
            notificationExample.createCriteria().andReceiverEqualTo(userId);
            notificationExample.setOrderByClause("gmt_create desc");
            List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(notificationExample, new RowBounds(offset, size));
            if (notifications.size() == 0) {
                return paginationDTO;
            }
            List<NotificationDTO> notificationDTOS = new ArrayList<>();
            for (Notification notification : notifications) {
                NotificationDTO notificationDTO = new NotificationDTO();
                BeanUtils.copyProperties(notification,notificationDTO);
                notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
                notificationDTOS.add(notificationDTO);
            }
            paginationDTO.setData(notificationDTOS);
        } else {
            page = 0;
        }
        paginationDTO.setPagination(totalPage, page);
        return paginationDTO;
    }

    public Long unreadCount(Long userId) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId).andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.countByExample(notificationExample);
    }

    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (!notification.getReceiver().equals(user.getId())) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);
        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification,notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }
}
