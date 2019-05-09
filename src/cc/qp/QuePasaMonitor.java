//Grupo: Nombre Ciprian Ilut (Matricula y160348), Nombre Jesus Vallejo Collados (Matricula y150319)
package cc.qp;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Iterator;
import es.upm.babel.cclib.Monitor;


public class QuePasaMonitor implements QuePasa {
	//Decalaracion de los Mapas de estructura
	private HashMap<String, Integer> creador;
	private HashMap<String, LinkedList<Integer>> grupos;
	private HashMap<Integer, LinkedList<Mensaje>> mensajes;
	private HashMap<Integer, LinkedList<Mensaje>> mLeidos;

	//Elementos auxiliares
	private	Iterator<Integer> itMandar;
	private PreconditionFailedException preFailE;
	private Mensaje msg;
	private LinkedList<Integer> bloqueados;

	//Monitor
	private Monitor mutex;
	private Monitor.Cond condM;

	/**
	 * Contructor de nuestro objeto
	 */
	public QuePasaMonitor() {
		this.creador =new HashMap<String, Integer>();
		this.grupos=new HashMap<String, LinkedList<Integer>>();
		this.mensajes=new HashMap<Integer, LinkedList<Mensaje>>();
		this.mLeidos=new HashMap<Integer, LinkedList<Mensaje>>();
		this.preFailE=new PreconditionFailedException();
		this.bloqueados=new LinkedList();
		this.mutex=new Monitor();
		this.condM= mutex.newCond();
	}

	/**
	 * Gracias a este metodo comprobaremos la invariante para comprobar si se
	 * cumple, es decir que el creador este en el grupo.
	 * @param uid
	 * @param grupo	
	 * @return
	 */
	private boolean invariante(int uid, String grupo) {
		if(creador.containsValue(uid) && grupos.get(grupo).contains(uid)) {
			return true;
		}
		return false;

	}
	/**
	 * Este metodo auxiliar lo utilizamos para obtener la posicion de un
	 * uid en una lista de miembros de un grupo, ya que al ser este un 
	 * int los metodos de las LinkedList las interpreta como indices.
	 * @param uid
	 * @param grupo
	 * @return
	 */
	private int searchPos(int uid ,String grupo) {
		mutex.enter();
		int pos =0;
		int size=grupos.get(grupo).size();
		for (int i =0; i<size;i++) {
			if (grupos.get(grupo).get(i).equals(uid)) {
				mutex.leave();
				return pos++;

			}
			else
			{
				pos++;
			}
		}
		mutex.leave();
		return -1;
	}

	/**
	 * Este metodo genera un nuevo grupo, comprobando la pre y en caso de 
	 * que se incumpla lanza un excepcion, de igualmanera lanzara una 
	 * excepticion en caso de que se viole la invariante
	 */
	public void crearGrupo(int creadorUid, String grupo) 
			throws PreconditionFailedException{

		if(creador.containsKey(grupo)) {
			throw preFailE;
		}
		mutex.enter();
		creador.put(grupo, creadorUid);
		grupos.put(grupo, new LinkedList<Integer>());
		grupos.get(grupo).add(creadorUid);

		mutex.leave();
		if(!invariante(creadorUid, grupo)) {
			throw preFailE;
		}

	}

	/**
	 * Este metodo ingresa un nuevo miembro en el grupo, en caso de que se
	 * cumpla la PRE, de no ser asi se lanzara una excepcion, ya que antes de 
	 * hacer ningun cambio se comprueba esta condicion.
	 */
	public void anadirMiembro(int creadorUid, String grupo, int nuevoMiembroUid)
			throws PreconditionFailedException{
		mutex.enter();

		if(invariante(creadorUid, grupo)&&
				!grupos.get(grupo).contains(nuevoMiembroUid)) {
			grupos.get(grupo).add(nuevoMiembroUid);
			if(grupos.get(grupo).contains(nuevoMiembroUid)) {
			}else {
				System.out.println("Estupido");
			}
			mutex.leave();

		}else {
			mutex.leave();
			throw preFailE;
		}
	}

	/**
	 * Este metodo le da la posibilidad a cualquier miembro a salir del 
	 * grupo, teneiendo como PRE que el miembro no sea el creador y debe
	 * pertenecer al grupo.
	 * En el momento que se elimina del grupo se eliminan los mensajes 
	 * que ha enviado.
	 * Como hemos organizado los mensajes en 
	 */

	public void salirGrupo(int miembroUid, String grupo)
			throws PreconditionFailedException{

		mutex.enter();
		if(grupos.containsKey(grupo)&&!invariante(miembroUid, grupo)
				&& grupos.get(grupo).contains(miembroUid)) {

			grupos.get(grupo).remove(searchPos(miembroUid,grupo));
			if(mensajes.containsKey(miembroUid) 
					&&!mensajes.get(miembroUid).isEmpty())
				mensajes.remove(miembroUid);
			
			if(mLeidos.containsKey(miembroUid) 
					&&!mLeidos.get(miembroUid).isEmpty())
				mLeidos.remove(miembroUid);

			mutex.leave();	
		}
		else {
			mutex.leave();
			throw preFailE;
		}

	}

	/**
	 * Metodo auxiliar para desbloquear los threat que no han cumplico la
	 * condicion pero ahora si la cumplen. Como el Monitor.cond genera una
	 * una lista FIFO, hemos creado una LinkedList de uids bloqueados con 
	 * iguales caracteristica, por tanto cuando se bloquea un threat 
	 * se introduce un elemento en la lista y cuando se desbloquea se elimina.
	 * @param uid
	 */
	private void signalCond(int uid) {
		mutex.enter();
		if(mensajes.containsKey(uid)&& !mensajes.get(uid).isEmpty()) {
			bloqueados.removeFirst();
			condM.signal();

		}
		mutex.leave();
	}
	/**
	 * Comprobamos si el remitente, pertenece al grupo, en caso de que sea asi 
	 * se introduce en la lista de mensajes de todos los integrantes del grupo.
	 * Para ello recorremos la lsita de integrantes del grupo y en caso de que 
	 * no pertenezca al mapa de mensajes se introduce con su mensaje. 
	 * Y comprobando si hay alguna lectura bloqueada, comprobando la lista de 
	 * usuarios bloqueados, y en caso de ser asi se desbloquea y llamando al 
	 * metodo anterior citado.
	 
	 */
	public void mandarMensaje(int remitenteUid, String grupo, Object contenidos)
			throws PreconditionFailedException{
		mutex.enter();

		if(grupos.containsKey(grupo)&&grupos.get(grupo).contains(remitenteUid)) {
			itMandar=grupos.get(grupo).iterator();
			msg=new Mensaje(remitenteUid, grupo, contenidos);
			while(itMandar.hasNext()) {
				int uidPertenece=itMandar.next();
				if(mensajes.containsKey(uidPertenece)) {
					mensajes.get(uidPertenece).add(msg);
				}else {
					mensajes.put(uidPertenece, new LinkedList<Mensaje>());
					mensajes.get(uidPertenece).add(msg);

				}
				if(!bloqueados.isEmpty()&&bloqueados.getFirst()==uidPertenece)
					signalCond(uidPertenece);		
			}


			mutex.leave();

		}else {
			mutex.leave();
			throw preFailE;
		}
	}

	/**
	 * Este metodo nos retorna el primer mensaje no leido por el usuario
	 * introducido. Para ello comprobamos el cumpliento de la CPRE y en 
	 * caso de no cumplirse, como anteriormente hemos dicho introducimos
	 * el uid dell elemeento bloqueado en la lista de bloqueados, y bloqueamos
	 * el threat, en caso de que exista mensaje, se obtiene el primer mensaje
	 * que la lsita de mensajes, se elimina de la lsita de mensajes y 
	 * se guarda en mLeidos. Posteriormente se retorna ese mensaje.
	 */

	public Mensaje leer(int uid) {
		mutex.enter();
		if(!mensajes.containsKey(uid)||mensajes.get(uid).isEmpty()) {
			bloqueados.add(uid);
			condM.await();
		}
		msg=mensajes.get(uid).getFirst();
		mensajes.get(uid).removeFirst();

		if(!mLeidos.containsKey(uid)) {
			mLeidos.put(uid, new LinkedList<Mensaje>());  
			mLeidos.get(uid).add(msg);                    
		}
		else {
			mLeidos.get(uid).add(msg);
		}
		mutex.leave();
		return msg;
	}
}