package com.lytquest.frogdemo.controller;

import com.lytquest.frogdemo.dto.ApiResponse;
import com.lytquest.frogdemo.entity.Book;
import com.lytquest.frogdemo.helper.ExcelHelper;
import com.lytquest.frogdemo.service.impl.BookServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin("http://localhost:8081")
@RestController
@RequestMapping("/api/v1")
public class BookController {

    private BookServiceImpl fileService;
    public BookController(BookServiceImpl fileService){
        this.fileService = fileService;
    }

    @PostMapping("/book-upload")
    @PreAuthorize("hasRole(1)")
    public ResponseEntity<ApiResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        if (ExcelHelper.hasExcelFormat(file)) {
            try {
                fileService.saveBook(file);
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Uploaded the file successfully: " + file.getOriginalFilename()));
            } catch (Exception e) {
                System.out.println("Error " + e);
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ApiResponse("Could not upload the file: " + file.getOriginalFilename() + "!"));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Please upload an excel file!"));
    }

    @GetMapping("/books")
    @PreAuthorize("hasRole(1)")
    public ResponseEntity<List<Book>> getAllBooks() {
        try {
            List<Book> books = fileService.getAllBooks();
            if (books.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
