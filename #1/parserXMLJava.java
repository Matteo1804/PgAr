/* La classe NODE è così definita
Attributi
  String ID;
	String label;
	boolean start=false,end=false;
	Vector<Node> linked_nodes;
  _analogo per i pesi dei rami_
Il metodo addNode, collega il nodo dato in input a quello attuale.
*/



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class fromXML2Nodes {
	
	//Attributi di classe
	private Vector<Node> tree;
	//private boolean lastimport = false;
	private File filename;
	
	//Costruttore
	public fromXML2Nodes(String filename) throws FileNotFoundException, XMLStreamException{
		try{
			this.filename=new File(filename);
		}catch(Exception e){
			System.out.println("File at "+filename+" is not avaiable or correctly patthed");
			return;
		}
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(this.filename));
		Node tmp = null;
		String data = "";
		String reference = "";
		String weight = "";
		while(reader.hasNext()){
			switch(reader.next()){
				case XMLStreamConstants.START_DOCUMENT:
					System.out.println("Start reading Doc");
					break;
					
				case XMLStreamConstants.START_ELEMENT:
					if("tree".equals(reader.getLocalName())){
						tree= new Vector<Node>();
						System.out.println("Start reading tree");
					}
					if("node".equals(reader.getLocalName())){
						tmp=new Node();
						//TODO improve detect attributes 
						if("start".equals(reader.getAttributeName(0)))
							if("true".equals(reader.getAttributeValue(0)))
								tmp.setStart();
						if("end".equals(reader.getAttributeName(0)))
							if("true".equals(reader.getAttributeValue(0)))
								tmp.setEnd();
					}
					if("edges".equals(reader.getLocalName())){
						reference=tree.lastElement().getID();
					}
					if("edge".equals(reader.getLocalName())){
						if("weight".equals(reader.getAttributeName(0).toString())){
							weight=reader.getAttributeValue(0).toString().trim();
							}
					}
					break;
					
				case XMLStreamConstants.CHARACTERS:
					if(reader.getText().trim().length()>0){
						data= reader.getText().trim();
					}
					break;
					
				case XMLStreamConstants.END_ELEMENT:
					switch(reader.getLocalName()){
						case "tree":
							System.out.println("Acquiring tree, completed!");
							break;
						case "node":
							tree.add(tmp);
							tmp=null;
							break;
						case "label":
							tmp.setID(data);
							tmp.setLabel(data);
							break;
						case "edge":
							//TODO maybe node still not exist
							tmp.addNode(getNodeByID(data), weight);
							weight="";
							break;
						case "edges":
							break;
					}
					break;
				
				case XMLStreamConstants.END_DOCUMENT:
					System.out.println("End reading Doc");
					break;
				
			}
		}
		
	}
	
	
	/*public boolean avaiable(){
		return lastimport;
	}*/
	
	private Node getNodeByID(String id){
		for(Node i:tree)
			if(i.getID().equals(id))
				return i;
		return null;
	}
	
	//Getter per la lista di Nodi creata dal parsing
	public Vector<Node> getNodi() {
		return tree;
	}
	
	
	//toString
	@Override
	public String toString(){
		String data="";
		for(Node i : tree)
			data+="\n"+i.toString();
		return data;
	}
	
	//Per salvare il grafo in XML
	public boolean save(String filename) throws XMLStreamException{
		System.out.println("Scrittura su file");
		XMLOutputFactory output = XMLOutputFactory.newInstance();
		XMLStreamWriter writer;
		try {
			writer = output.createXMLStreamWriter(new FileWriter(filename));

			writer.writeComment("data saved");
			writer.writeStartDocument("utf-8","1.0");
			//writer.setPrefix("", "");
			writer.writeStartElement("tree");
			for(Node i: tree){
				writer.writeStartElement("node");
				if(i.isStart()){
					writer.writeAttribute("start","true");
				}
				if(i.isEnd()){
					writer.writeAttribute("end","true");
				}
				writer.writeStartElement("label");
				writer.writeCharacters(i.getLabel());
				writer.writeEndElement();// End label
				if(i.hasEdge()){
					writer.writeStartElement("edges");
					for(Node j: i.getEdge()){
						writer.writeStartElement("edge");
						writer.writeAttribute("weight",i.getWeightEdge(j.getID()));
						writer.writeCharacters(j.getID());
						writer.writeEndElement();//end Edge
					}
					writer.writeEndElement(); // End edges
				}
				writer.writeEndElement(); // End node
			}
			writer.writeEndElement(); // End Tree
			writer.writeEndDocument(); //End Document
			writer.flush();
			writer.close();
			System.out.println("End!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.print("Vecchio, problema!");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
