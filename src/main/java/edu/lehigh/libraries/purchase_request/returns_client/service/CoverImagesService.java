package edu.lehigh.libraries.purchase_request.returns_client.service;

import org.springframework.stereotype.Service;

@Service
public class CoverImagesService {
  
    public String getCoverImage(String isbn) {
        return "https://covers.openlibrary.org/b/isbn/" + isbn + "-L.jpg";
    }

}
