package com.study.MessagePack.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.msgpack.MessagePack;
import org.msgpack.template.Template;
import org.msgpack.template.Templates;

public class MessagePackTest {

	public static void main(String[] args) throws IOException {
		
		List<String> list = new ArrayList<String>();
		list.add("123123");
		list.add("MessagePack");
		list.add("Test");
		
		
		MessagePack mp = new MessagePack();
		
		byte[] raw = mp.write(list);
		
		
		
		List<String> resultList = mp.read(raw,Templates.tList(Templates.TString));
		if(resultList != null){
			System.out.println("resultList.size()=" + resultList.size());
			
			for (String string : resultList) {
				System.out.println(string);
			}
			
		}
		
		
		
	}

}
