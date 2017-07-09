/**
 * Copyright (c) 2014, MindFusion LLC - Bulgaria.
 */

import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.mindfusion.diagramming.*;


public class Diagrama extends JFrame
{
	public static void main(String[] args)
	{
		Diagrama mainFrame = new Diagrama();
		mainFrame.setVisible(true);
	}

	public Diagrama()
	{
		super("Graficar desde un XML");

		// set up the main window
		setBounds(0, 0, 800, 550);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// create diagram and view 
		final Diagram diagram = new Diagram();
		DiagramView view = new DiagramView();
		view.setDiagram(diagram);

		// add scroll pane
		JScrollPane scrollPane = new JScrollPane(view);
		scrollPane.setVisible(true);
		getContentPane().add(scrollPane);

		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowOpened(WindowEvent e)
			{
				loadGraph(diagram, "./src/diagrama.xml");

				HtmlBuilder creador = new HtmlBuilder(diagram);
				try {
					String text = creador.createImageHtml("index.html","Code2Chart","./diagrama.png", "./diagrama.png", "png");
					System.out.println(text);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}
	void loadGraph(Diagram diagram, String filepath)
	{
		NodoHandler manejador = new NodoHandler();
		HashMap<String, DiagramNode> nodeMap = new HashMap<String, DiagramNode>();
		Rectangle2D.Float bounds = new Rectangle2D.Float(0, 0, 15, 8);
		
		// load the graph XML
		Document document = loadXmlFile(filepath);
		Element root = document.getDocumentElement();

		// load node data
		NodeList nodes = root.getElementsByTagName("Node");
		for (int i = 0; i < nodes.getLength(); ++i)
		{
			Element node = (Element)nodes.item(i);
			ShapeNode diagramNode = diagram.getFactory().createShapeNode(bounds);
			manejador.conversor(node,diagramNode);
			String idNodo = node.getAttribute("id");
			nodeMap.put(idNodo, diagramNode);
			diagramNode.setText(idNodo);
			diagramNode.setText(node.getAttribute("nombre"));

		}

		// load link data
		NodeList links = root.getElementsByTagName("Link");
		for (int i = 0; i < links.getLength(); ++i)
		{
			Element link = (Element)links.item(i);
			DiagramNode origin = nodeMap.get(link.getAttribute("origin"));
			DiagramNode target = nodeMap.get(link.getAttribute("target"));
			diagram.getFactory().createDiagramLink(origin, target);
		}

		// arrange the graph
		LayeredLayout layout = new LayeredLayout();
		layout.setLayerDistance(12);
		layout.arrange(diagram);
	}

	Document loadXmlFile(String filepath)
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);
		factory.setNamespaceAware(true);

		Document document = null;
		DocumentBuilder builder;
		try
		{
			File file = new File(filepath);
			builder = factory.newDocumentBuilder();
			document = builder.parse(file); 
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return document;
	}
}