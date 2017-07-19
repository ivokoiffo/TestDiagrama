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

//if (newDecision)
//{
//	newNode.setShape(Shape.fromId("Decision"));
//	newNode.setAnchorPattern(AnchorPattern.fromId("Decision2In2Out"));
//	newNode.setBrush(decisionBrush);
//	newNode.setText("Decision");
//	newNode.setTag(true);
//}

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

		// traigo todos los nodos, y todos los links
		NodeList nodes = root.getElementsByTagName("Node");
		NodeList links = root.getElementsByTagName("Link");
		
		List<String> nodosDecision = new ArrayList<String>(); //aca voy a storear los ids de todos los nodos que son decision
		List<String> nodosNoDecision = new ArrayList<String>();
		
		for (int i = 0; i < nodes.getLength(); ++i)
		{
			Element node = (Element)nodes.item(i);
			//nuevocodigo
			String tipo = node.getAttribute("tipo");
			switch (tipo) {
			case "decision":
				nodosDecision.add(node.getAttribute("id"));
		 		break;
		 	
		 	default:
		 		nodosNoDecision.add(node.getAttribute("id"));
		 		break;
			}
			
			ShapeNode diagramNode = diagram.getFactory().createShapeNode(bounds);
			//Convierte el "tipo" ubicado en el xml en la forma
			manejador.conversor(node,diagramNode);
			String idNodo = node.getAttribute("id");
			nodeMap.put(idNodo, diagramNode);
			diagramNode.setText(idNodo);
			diagramNode.setText(node.getAttribute("nombre"));
			//Clave para que se vea bien el texto dentro del nodo
			diagramNode.resizeToFitText(FitSize.KeepRatio);
		}

		
		
		List<String> nodosYaLinkeados = new ArrayList<String>(); //para mapear de 1 sola vez
		// mapeo los links
		for (int i = 0; i < links.getLength(); ++i)
		{
			
			Element link = (Element)links.item(i);
			DiagramNode origin = nodeMap.get(link.getAttribute("origin"));
			if (!esNodoDecision(link.getAttribute("origin"), nodosDecision)){
				//es un nodo comun
				DiagramNode target = nodeMap.get(link.getAttribute("target"));
				diagram.getFactory().createDiagramLink(origin, target);
				nodosYaLinkeados.add(link.getAttribute("origin"));
			}
			else
			{
				//primero me fijo si ya fueron mapeados sus links
				//entrando a esta parte significa que es un nodo de decision
				if (!nodosYaLinkeados.contains(link.getAttribute("origin"))) {
				List<String> idsTarget = new ArrayList<String>();
				idsTarget = obtenerNodosTargetsDadoUnNodoOrigenDeDecision(link.getAttribute("origin"), links);
				DiagramNode target1 = nodeMap.get(idsTarget.get(0));
				DiagramNode target2 = nodeMap.get(idsTarget.get(1));
				diagram.getFactory().createDiagramLink(origin, target1).setText("SI");
				diagram.getFactory().createDiagramLink(origin, target2).setText("NO");
				nodosYaLinkeados.add(link.getAttribute("origin"));
				}
			}
		}

		// Conn esto, menciono que si bien tome un layout de Decision, tambien tengo que mapear todas las relaciones de cada
		//uno de los nodos, es decir si hay uno que es decision, necesariamente tengo que crear los 2 links de decision seguidos,
		//no uno, y luego otro.
		DecisionLayout layout = new DecisionLayout();
		layout.setHorizontalPadding(10);
		layout.setVerticalPadding(10);
		layout.arrange(diagram);
	}

	
	boolean esNodoDecision(String idNodo, List<String> nodosDecision) {
		boolean decision = false;
		if (nodosDecision.contains(idNodo)){
			decision = true;	
		}
		
		return decision;	
	}
	
	
	
	List<String> obtenerNodosTargetsDadoUnNodoOrigenDeDecision(String idNodoDecision, NodeList links) {
		
		List<String> nodosTarget = new ArrayList<String>();
		for (int i = 0; i < links.getLength(); ++i)
		{
			Element link = (Element)links.item(i);
			String idorigen = link.getAttribute("origin");
			if (idorigen.equals(idNodoDecision)) {
				nodosTarget.add(link.getAttribute("target"));
			}

		}
		
		return nodosTarget;
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