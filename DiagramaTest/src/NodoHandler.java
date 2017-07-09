import org.w3c.dom.Element;

import com.mindfusion.diagramming.Shape;
import com.mindfusion.diagramming.ShapeNode;

public class NodoHandler {
	public NodoHandler() {}
	public void conversor(Element nodo,ShapeNode diagramaNodo) {
		String tipo = nodo.getAttribute("tipo");
		switch (tipo) {		 
		        case "inicio":
		        	diagramaNodo.setShape(Shape.fromId("Terminator"));
		        break;
	
		        case "decision":
		        	diagramaNodo.setShape(Shape.fromId("Decision"));
			    break;
			    
		        case "fin":
		        	diagramaNodo.setShape(Shape.fromId("Terminator"));
		        break;
			    /*    
		        case "declaracion":
		        instrucciones;
		        break;
		        
		        case "asignacion":
			        instrucciones;
			        break;
			    
		        case "entrada":
			        instrucciones;
			    break;    
			        
		        case "salida":
			        instrucciones;
			    break;
			        
			    
		        case "fin":
			        instrucciones;
			    break;
			     */
		 }
 
	}
}
