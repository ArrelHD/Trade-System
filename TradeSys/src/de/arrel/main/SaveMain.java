package de.arrel.main;

import java.io.*;
import java.text.DateFormat;
import java.util.GregorianCalendar;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;




public class SaveMain implements Listener{
	//private CraftingManager config = new CraftingManager(); 
	private String logpath = "plugins/WebServer/web/Stadtlog.html";
	private File log = new File(logpath);
	
	
	public void log(String logtxt) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(log, true));
			
	        GregorianCalendar now = new GregorianCalendar();
	        DateFormat date = DateFormat.getDateInstance(DateFormat.MEDIUM);   			 // 14.04.12 
	        DateFormat time = DateFormat.getTimeInstance(DateFormat.MEDIUM);             // 21:21:12 
			    
			writer.write("-" + date.format(now.getTime()) + " - " + time.format(now.getTime()) + "- " + logtxt);
			writer.newLine();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//FILE SPEICHERUNG
	public boolean saveFile(String path, String name, String data) {
		File file = new File(path);
		checkPath(file);
		YamlConfiguration filecon = YamlConfiguration.loadConfiguration(file);
		filecon.set(name, data);
		try {
			filecon.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	public boolean saveFile(String path, String name, boolean data) {
		File file = new File(path);
		checkPath(file);
		YamlConfiguration filecon = YamlConfiguration.loadConfiguration(file);
		filecon.set(name, data);
		
		try {
			filecon.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	public boolean saveFile(String path, String name, long data) {
		File file = new File(path);
		checkPath(file);
		YamlConfiguration filecon = YamlConfiguration.loadConfiguration(file);
		filecon.set(name, data);
		try {
			filecon.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	
	public String getStringFile(String path, String name) {
		File file = new File(path);
		checkPath(file);
		YamlConfiguration filecon = YamlConfiguration.loadConfiguration(file);
		if(filecon.isSet(name)) {
			return filecon.getString(name);
		}else {
			return null;
		}
	}
	
	public boolean getBooleanFile(String path, String name) {
		File file = new File(path);
		checkPath(file);
		YamlConfiguration filecon = YamlConfiguration.loadConfiguration(file);
		return filecon.getBoolean(name);
	}
	
	public boolean checkPath(File file) {
		if(!new File(file.getParent()).exists()) {
			new File(file.getParent()).mkdirs();
			System.out.println("neuer Path wurde erstellt!");
		}
		if(!file.exists()) {
			try {
				file.createNewFile();
				System.out.println("neue File wurde erstellt!");
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	//ENDE
	
	public void clearFile(String path) {
		File file = new File(path);
		if(!checkPath(file)) {
			file.delete();
			checkPath(file);
		}
	}
}
