package com.lytquest.frogdemo.service.impl;

import com.lytquest.frogdemo.entity.Book;
import com.lytquest.frogdemo.helper.ExcelHelper;
import com.lytquest.frogdemo.helper.TaskThread;
import com.lytquest.frogdemo.helper.ThreadPoolExecutorUtil;
import com.lytquest.frogdemo.repository.BookRepository;
import com.lytquest.frogdemo.service.BookService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final ThreadPoolExecutorUtil threadPoolExecutorUtil;
    private BookRepository repository;

    public BookServiceImpl(BookRepository repository, ThreadPoolExecutorUtil threadPoolExecutorUtil){
        this.repository = repository;
        this.threadPoolExecutorUtil = threadPoolExecutorUtil;
    }


    @Override
    @Scheduled(cron="0 6 * * * *")
    public void saveBook(MultipartFile file) {
        try {
            List<Book> books = ExcelHelper.readExcel(file.getInputStream());
            repository.saveAll(books);
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }

    @Override
    public List<Book> getAllBooks() {
        return repository.findAll();
    }

    @Override
    public List<Book> getAllBookAsync() {
        for (int i=0;i<20000;i++)
        {
            TaskThread taskThread=new TaskThread(repository);
            threadPoolExecutorUtil.executeTask(taskThread);
        }
        /*
            Following code created to just return list of values at the end
         */
        TaskThread taskThread = new TaskThread(repository);
        threadPoolExecutorUtil.executeTask(taskThread);
        return taskThread.books;
    }


}