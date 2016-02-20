package com.art.fine.oneal.web;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.art.fine.oneal.domain.ImageFile;

@RequestMapping("/renderimage/**")
@Controller
public class RenderImageController {

    @RequestMapping
    public void get(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
    	
    	Long id = new Long((String)request.getParameter("id"));
    	ImageFile imageFile = ImageFile.findImageFile(id);
    	response.setContentType("image/jpeg");
    	
    	if(imageFile != null)
    	{
    		try{
    			InputStream in = new ByteArrayInputStream(imageFile.getFile());
    			BufferedImage bufferedImage = ImageIO.read(in);
    			OutputStream outputStream = response.getOutputStream();
    			ImageIO.write(bufferedImage, "jpeg", outputStream);
    			outputStream.close();
    		}
    		catch(IOException ioe){
    			ioe.printStackTrace();
    		}
    	}
    }

    @RequestMapping(method = RequestMethod.POST, value = "{id}")
    public void post(@PathVariable Long id, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
    }

}
