package com.sax;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Le gestionnaire d'�v�nements est une classe qui h�rite de DefaultHandler
 * 
 * @author TBOUDAA
 *
 */
public class XMLHandler extends DefaultHandler {

	private String node = null;


	
	/**
	 * M�thode qui va s'executer au d�but du parcing 
	 */
	public void startDocument() throws SAXException {
		System.out.println("D�but du parsing");
	}

	/**
	 * M�thode qui va s'executer � la fin du parsing
	 */
	public void endDocument() throws SAXException {
		System.out.println("Fin du parsing");
	}

	/**
	 * s'execute au d�but de chaque �l�ment
	 */
	public void startElement(String namespaceURI, String lname, String nodeName,
			Attributes litsAttributs) throws SAXException {

		System.out.println("-----------------ELEMENT--------------------");
		
		
		// cette variable contient le nom du n�ud qui a d�clench� l'�v�nement
		
		System.out.println("nom du noeud qui a d�clench� l'�v�nement = " + nodeName);
		
		node = nodeName;

		// Cette derni�re contient la liste des attributs du n�ud
		if (litsAttributs != null) {
			for (int i = 0; i < litsAttributs.getLength(); i++) {
				// nous r�cup�rons le nom de l'attribut
				String aname = litsAttributs.getLocalName(i);
				// Et nous affichons sa valeur
				System.out.println("Attribut " + aname + " valeur : "
						+ litsAttributs.getValue(i));
			}
		}
	}
	
	
	
	/**
	 * permet de r�cup�rer la valeur d'un n�ud
	 */  
	public void characters(char[] data, int start, int end){   
	   System.out.println("*******************characters****************************");
	
	   
	   System.out.println(start);
	   System.out.println(end);
	   
	   String str = new String(data, start, end);
	   
	   System.out.println("Donn�e du n�ud " + node + " : " + str);
	   
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		System.out.println("Fin de l'�l�ment " + qName);
	}
}