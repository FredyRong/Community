package com.fredy.community.service;

import com.fredy.community.dto.PaginationDTO;
import com.fredy.community.dto.QuestionDTO;
import com.fredy.community.dto.QuestionQueryDTO;
import com.fredy.community.exception.CustomizeErrorCode;
import com.fredy.community.exception.CustomizeException;
import com.fredy.community.mapper.QuestionExtMapper;
import com.fredy.community.mapper.QuestionMapper;
import com.fredy.community.mapper.UserMapper;
import com.fredy.community.model.Question;
import com.fredy.community.model.QuestionExample;
import com.fredy.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    @Autowired(required = false)
    private QuestionMapper questionMapper;
    @Autowired(required = false)
    private UserMapper userMapper;
    @Autowired(required = false)
    private QuestionExtMapper questionExtMapper;

    public PaginationDTO list(String search, Integer page, Integer size) {
        if (StringUtils.isNotBlank(search)) {
            String[] tags = StringUtils.split(search, " ");
            search = Arrays.stream(tags).collect(Collectors.joining("|"));
        }
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);
        Integer totalPage = (int) Math.ceil(totalCount / 1.0 / size);

        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
        if (totalCount != 0) {
            page = Math.max(1, page);
            page = Math.min(totalPage, page);
            Integer offset = size * (page - 1);

            questionQueryDTO.setOffset(offset);
            questionQueryDTO.setSize(size);
            List<Question> questions = questionExtMapper.selectBySearch(questionQueryDTO);
            List<QuestionDTO> questionDTOList = new ArrayList<>();
            for (Question question : questions) {
                User user = userMapper.selectByPrimaryKey(question.getCreator());
                QuestionDTO questionDTO = new QuestionDTO();
                BeanUtils.copyProperties(question, questionDTO);
                questionDTO.setUser(user);
                questionDTOList.add(questionDTO);
            }
            paginationDTO.setData(questionDTOList);
        } else {
            page = 0;
        }
        paginationDTO.setPagination(totalPage, page);
        return paginationDTO;
    }

    public PaginationDTO list(Long userId, Integer page, Integer size) {
        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        Integer totalCount = (int) questionMapper.countByExample(example);
        Integer totalPage = (int) Math.ceil(totalCount / 1.0 / size);

        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
        if (totalCount != 0) {
            page = Math.max(1, page);
            page = Math.min(totalPage, page);
            Integer offset = size * (page - 1);

            QuestionExample questionExample = new QuestionExample();
            questionExample.createCriteria().andCreatorEqualTo(userId);
            questionExample.setOrderByClause("gmt_create desc");
            List<Question> questions = questionMapper.selectByExampleWithRowbounds(questionExample, new RowBounds(offset, size));

            List<QuestionDTO> questionDTOList = new ArrayList<>();
            for (Question question : questions) {
                User user = userMapper.selectByPrimaryKey(question.getCreator());
                QuestionDTO questionDTO = new QuestionDTO();
                BeanUtils.copyProperties(question, questionDTO);
                questionDTO.setUser(user);
                questionDTOList.add(questionDTO);
            }
            paginationDTO.setData(questionDTOList);
        } else {
            page = 0;
        }
        paginationDTO.setPagination(totalPage, page);
        return paginationDTO;
    }

    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        } else {
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample example = new QuestionExample();
            example.createCriteria().andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(updateQuestion, example);
            if (updated != 1) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    public void incView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if (StringUtils.isBlank(queryDTO.getTag())) {
            return new ArrayList<>();
        }
        String[] tags = StringUtils.split(queryDTO.getTag(), ",");
        String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);
        List<Question> questions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }
}

