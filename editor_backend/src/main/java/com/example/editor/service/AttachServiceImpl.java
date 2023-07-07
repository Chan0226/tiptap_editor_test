package com.example.editor.service;

import com.example.editor.model.AttachmentVo;
import com.example.editor.repository.AttachDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class AttachServiceImpl implements AttachService{
    @Autowired
    AttachDao attachDao;

    @Override
    public void insertAttach(AttachmentVo attachmentVo) {
        attachDao.insertAttach(attachmentVo);
    }
}
