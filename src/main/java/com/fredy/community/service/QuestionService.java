package com.fredy.community.service;

import com.fredy.community.dto.PaginationDTO;
import com.fredy.community.dto.QuestionDTO;
import com.fredy.community.exception.CustomizeErrorCode;
import com.fredy.community.exception.CustomizeException;
import com.fredy.community.mapper.QuestionExtMapper;
import com.fredy.community.mapper.QuestionMapper;
import com.fredy.community.mapper.UserMapper;
import com.fredy.community.model.Question;
import com.fredy.community.model.QuestionExample;
import com.fredy.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired(required = false)
    private QuestionMapper questionMapper;
    @Autowired(required = false)
    private UserMapper userMapper;
    @Autowired(required = false)
    private QuestionExtMapper questionExtMapper;

    public PaginationDTO list(Integer page, Integer size) {
        Integer totalCount = (int) questionMapper.countByExample(new QuestionExample());
        Integer totalPage = (int) Math.ceil(totalCount / 1.0 / size);

        PaginationDTO paginationDTO = new PaginationDTO();
        if(totalCount != 0){
            page = Math.max(1, page);
            page = Math.min(totalPage, page);
            Integer offset = size * (page - 1);

            List<Question> questions = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, size));
            List<QuestionDTO> questionDTOList = new ArrayList<>();
            for (Question question : questions) {
                User user = userMapper.selectByPrimaryKey(question.getCreator());
                QuestionDTO questionDTO = new QuestionDTO();
                BeanUtils.copyProperties(question, questionDTO);
                questionDTO.setUser(user);
                questionDTOList.add(questionDTO);
            }
            paginationDTO.setQuestions(questionDTOList);
        }else{
            page = 0;
        }
        paginationDTO.setPagination(totalPage, page);
        return paginationDTO;
    }

    public PaginationDTO list(Integer userId, Integer page, Integer size) {
        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        Integer totalCount = (int) questionMapper.countByExample(example);
        Integer totalPage = (int) Math.ceil(totalCount / 1.0 / size);

        PaginationDTO paginationDTO = new PaginationDTO();
        if(totalCount != 0){
            page = Math.max(1, page);
            page = Math.min(totalPage, page);
            Integer offset = size * (page - 1);

            QuestionExample example1 = new QuestionExample();
            example.createCriteria().andCreatorEqualTo(userId);
            List<Question> questions = questionMapper.selectByExampleWithRowbounds(example1, new RowBounds(offset, size));

            List<QuestionDTO> questionDTOList = new ArrayList<>();
            for (Question question : questions) {
                User user = userMapper.selectByPrimaryKey(question.getCreator());
                QuestionDTO questionDTO = new QuestionDTO();
                BeanUtils.copyProperties(question, questionDTO);
                questionDTO.setUser(user);
                questionDTOList.add(questionDTO);
            }
            paginationDTO.setQuestions(questionDTOList);
        }else{
            page = 0;
        }
        paginationDTO.setPagination(totalPage, page);
        return paginationDTO;
    }

    public QuestionDTO getById(Integer id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if(question == null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if(question.getId() == null){
            questionMapper.insert(question);
        }else{
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample example = new QuestionExample();
            example.createCriteria().andCreatorEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(updateQuestion, example);
            if (updated != 1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        }
    }

    public void incView(Integer id) {
        Question question = new Question();
        question.setId(id);
        questionExtMapper.incView(question);
    }
}

