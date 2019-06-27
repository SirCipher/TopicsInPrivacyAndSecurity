
/*
 *@author  William_Wilson
 *@version 1.6
 *Created: May 8, 2007
 */

/*
 *import list
 */
import java.io.File;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBufferByte;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/*
 *Class Steganography_Abstract
 */
public abstract class Steganography_Abstract
{
	
	/*
	 *Steganography_Abstract Empty Constructor
	 */
	public Steganography_Abstract()
	{
	}
	
	/*
	 *Encrypt an image with text, the output file will be of type .png
	 *@param path		 The path (folder) containing the image to modify
	 *@param original	The name of the image to modify
	 *@param ext1		  The extension type of the image to modify (jpg, png)
	 *@param stegan	  The output name of the file
	 *@param message  The text to hide in the image
	 *@param type	  integer representing either basic or advanced encoding
	 */
	public boolean encode(String path, String original, String ext1, String stegan, String message)
	{
		String			file_name 	= image_path(path,original,ext1);
		BufferedImage 	image_orig	= getImage(file_name);
		
		//user space is not necessary for Encrypting
		BufferedImage image = user_space(image_orig);
		image = add_text(image,message);
		
		return (image==null)?false:(setImage(image,new File(image_path(path,stegan,"png")),"png"));
	}
	
	/*
	 *Decrypt assumes the image being used is of type .png, extracts the hidden text from an image
	 *@param path   The path (folder) containing the image to extract the message from
	 *@param name The name of the image to extract the message from
	 *@param type integer representing either basic or advanced encoding
	 */
	public String decode(String path, String name)
	{
		byte[] decode;
		try
		{
			//user space is necessary for decrypting
			BufferedImage image  = user_space(getImage(image_path(path,name,"png")));
			decode = extract_hidden_text(get_byte_data(image));
			return(new String(decode));
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, 
				"There is no hidden message in this image!","Error",
				JOptionPane.ERROR_MESSAGE);
			return "";
		}
	}
	
	/*
	 *Returns the complete path of a file, in the form: path\name.ext
	 *@param path   The path (folder) of the file
	 *@param name The name of the file
	 *@param ext	  The extension of the file
	 *@return A String representing the complete path of a file
	 */
	private String image_path(String path, String name, String ext)
	{
		return path + "/" + name + "." + ext;
	}
	
	/*
	 *Get method to return an image file
	 *@param f The complete path name of the image.
	 *@return A BufferedImage of the supplied file path
	 *@see	Steganography.image_path
	 */
	private BufferedImage getImage(String f)
	{
		BufferedImage 	image	= null;
		File 		file 	= new File(f);
		
		try
		{
			image = ImageIO.read(file);
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(null, 
				"Image could not be read!","Error",JOptionPane.ERROR_MESSAGE);
		}
		return image;
	}
	
	/*
	 *Set method to save an image file
	 *@param image The image file to save
	 *@param file	  File  to save the image to
	 *@param ext	  The extension and thus format of the file to be saved
	 *@return Returns true if the save is succesful
	 */
	private boolean setImage(BufferedImage image, File file, String ext)
	{
		try
		{
			file.delete(); //delete resources used by the File
			ImageIO.write(image,ext,file);
			return true;
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, 
				"File could not be saved!","Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	/*
	 *Handles the addition of text into an image
	 *@param image The image to add hidden text to
	 *@param text	 The text to hide in the image
	 *@return Returns the image with the text embedded in it
	 */
	private BufferedImage add_text(BufferedImage image, String text)
	{
		//convert all items to byte arrays: image, message, message length
		byte img[]  = get_byte_data(image);
		try
		{
			insert_hidden_text(img, text); 
			return image;
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, "Target File cannot hold message!", "Error",JOptionPane.ERROR_MESSAGE);
			return null;
		}
		//return image;
	}

	/*
	 *Creates a user space version of a Buffered Image, for editing and saving bytes
	 *@param image The image to put into user space, removes compression interferences
	 *@return The user space version of the supplied image
	 */
	private BufferedImage user_space(BufferedImage image)
	{
		//create new_img with the attributes of image
		BufferedImage new_img  = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D	graphics = new_img.createGraphics();
		graphics.drawRenderedImage(image, null);
		graphics.dispose(); //release all allocated memory for this image
		return new_img;
	}
	
	/*
	 *Gets the byte array of an image
	 *@param image The image to get byte data from
	 *@return Returns the byte array of the image supplied
	 *@see Raster
	 *@see WritableRaster
	 *@see DataBufferByte
	 */
	private byte[] get_byte_data(BufferedImage image)
	{
		WritableRaster raster   = image.getRaster();
		DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
		return buffer.getData();
	}
	
	/*
	 * Insert a String into an array of image bytes
	 * @param image	    Array of data representing an image
	 * @param text      String containing the secret text
	 */
	protected abstract void insert_hidden_text(byte[] img, String text);
		
	/*
	 *Retrieves hidden text from an image
	 *@param image Array of data, representing an image
	 *@return Array of data which contains the hidden text
	 */
	protected abstract byte[] extract_hidden_text(byte[] image); 
}



