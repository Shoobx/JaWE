package org.enhydra.jawe;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.enhydra.shark.utilities.SequencedHashMap;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;

/**
 * Creates button witch displays popup with available choices for showing xpdl objects.
 * Added functionnalities to automatically support 
 *
 * @author Sasa Bojanic
 * @author Miroslav Popov
 */
public class XMLElementChoiceButton extends ChoiceButton implements ActionListener {
	private static int MODE_NONE = 0;
	private static int MODE_LONG_LIST = 1;
	private static int MODE_SHORT_LIST = 2;	
	private static int MIN_LIST_SIZE=30;
	
	protected ChoiceButtonListener parent;	
	protected int lastMode = MODE_NONE;	
	protected SequencedHashMap choiceMap=new SequencedHashMap();
	protected JPopupMenu popup=new JPopupMenu();
	
	protected JTextField filterField = new JTextField();
	protected FilteredListModel filterModel = new FilteredListModel();
	protected JList longList;
	
	protected Class choiceType;
	protected String[] filterDatas;
	
    protected ImageIcon defIcon=null;
       
    protected boolean alwaysUseDefaultIcon=true;

    protected static Icon select;

    static {
       try {
         select = new ImageIcon(XMLElementChoiceButton.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/select_icon_small.gif"));
       } catch (Exception e) {
       }
    } 
    
	public XMLElementChoiceButton(Class choiceType,ChoiceButtonListener parent,ImageIcon icon, boolean alwaysUseDefaultIcon) {
		this(choiceType,parent,icon,alwaysUseDefaultIcon,null);
	}
	/**
	 * Create a choice button. If filterDatas is not null, this is is a list of element childs
	 * names that will be used to filter.
	 * @param choiceType
	 * @param parent
	 * @param icon
	 * @param filterDatas
	 */
	public XMLElementChoiceButton(Class choiceType,ChoiceButtonListener parent,ImageIcon icon, boolean alwaysUseDefaultIcon, String[] filterDatas) {
		this.parent = parent;
		this.choiceType=choiceType;
		this.filterDatas=filterDatas;
		
        this.defIcon=icon;
        this.alwaysUseDefaultIcon=alwaysUseDefaultIcon;
		setIcon(icon);
		addActionListener(this);
		setBorderPainted(false);
		setMargin(new Insets(1, 2, 1, 2));
		setSize(new Dimension(10, 8));
		setAlignmentY(0.5f);
		
		longList = new JList(filterModel){
			public String getToolTipText(MouseEvent evt) {
				int index = locationToIndex(evt.getPoint());
				Object item = getModel().getElementAt(index);
				return getElementTooltipText((XMLElement)((Map.Entry)item).getValue());
			}
		};
		filterModel.setFilteredList(choiceMap.entrySet().toArray());
		filterField.getDocument().addDocumentListener(filterModel);
		filterField.addActionListener(filterModel);
		longList.setCellRenderer(new ChoiceButtonCellRenderer());
		longList.setVisibleRowCount(MIN_LIST_SIZE);
		longList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if ( (e.getSource()==longList) ){
					int item = longList.locationToIndex(e.getPoint());
					if (item>=0)
					setSelectedItem(((Map.Entry)filterModel.getElementAt(item)).getValue());
				}

			}
		});  
		longList.addKeyListener(new KeyAdapter() {
			   public void keyReleased(KeyEvent ke) {
			    Object o = longList.getSelectedValue();
			    if ( (ke.getKeyCode() == KeyEvent.VK_ENTER) && (o!=null))
			    	setSelectedItem(((Map.Entry)o).getValue());
			    }
			   });
	}
	
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == this) {
			refresh();
			if (choiceMap.size() > 0) {
				popup.show(this.getParent(), this.getX(), this.getY()+this.getHeight());
				filterField.grabFocus();
			}
		} else {
			JMenuItem selected = (JMenuItem) ae.getSource();
			int sel=popup.getComponentIndex(selected);
			Object obj=choiceMap.getValue(sel);
			setSelectedItem(obj);
		}
		
	}
	
	private void setSelectedItem(Object obj){

		parent.selectionChanged(this,obj);
		choiceMap.clear();
		popup.setVisible(false);
		setObjectIcon(obj);
    }
	
    public void setObjectIcon (Object obj) {
       if (obj instanceof XMLElement && !alwaysUseDefaultIcon) {
          ImageIcon ic=JaWEManager.getInstance().getJaWEController().getTypeResolver().getJaWEType((XMLElement)obj).getIcon();           
          if (ic==null) {
             ic=defIcon;
          } else {
             if(select!=null) {
                ic=new CombinedIcon(ic,select,CombinedIcon.POS_BOTTOM_RIGHT);
             }
          }
          setIcon(ic);
       }       
    }
    
	protected void refresh () {
		choiceMap.clear();
		List choices=parent.getChoices(this);
		if (choices != null) {
	         Iterator it = choices.iterator();
	         while (it.hasNext()) {
	            XMLElement choice = (XMLElement) it.next();
	            String dispName = " "
	                              + JaWEManager.getInstance()
	                                 .getDisplayNameGenerator()
	                                 .getDisplayName(choice) + " ";
	            if (choiceMap.containsKey(dispName)) {
	               if (choice instanceof XMLComplexElement) {
	                  XMLElement idEl = ((XMLComplexElement) choice).get("Id");
	                  dispName += "["
	                              + ResourceManager.getLanguageDependentString("IdKey") + "="
	                              + idEl.toValue() + "] ";
	               }
	            }
	            choiceMap.put(dispName, choice);
	         }
	      }
		if (choiceMap.size()>=MIN_LIST_SIZE){
			filterModel.setFilteredList(choiceMap.entrySet().toArray());
			if (lastMode !=MODE_LONG_LIST){
				popup.removeAll();
				filterField.setText("");
				JScrollPane p = new JScrollPane(longList);
				popup.add(filterField);
				popup.add(p);
			}
			lastMode=MODE_LONG_LIST;
		}
		else if (choiceMap.size() > 0) {
			popup.removeAll();
			String[] names = new String[choiceMap.size()];
			choiceMap.keySet().toArray(names);
			for (int i = 0; i < choiceMap.size(); i++) {
				JMenuItem mi = new JMenuItem(names[i]);
				mi.setToolTipText(getElementTooltipText((XMLElement)choiceMap.get(names[i])));
				popup.add(mi);
				mi.addActionListener(this);
			}
			lastMode=MODE_SHORT_LIST;
		}
	}
	
	public Class getChoiceType () {
		return choiceType;
	}
	
	private String getElementTooltipText(XMLElement element){
		return JaWEManager.getInstance().getTooltipGenerator().getTooltip(element);
	}
	private class ChoiceButtonCellRenderer extends JLabel implements ListCellRenderer{
		Font bold;
		Font normal;
		public ChoiceButtonCellRenderer(){
			Font f=getFont();
			bold = f.deriveFont(f.getStyle() | Font.BOLD);
			normal = f.deriveFont(f.getStyle() & (~Font.BOLD));
			
		}
		/**
		 * returns the cell renderer use for the JList
		 */
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			String key = ((Map.Entry)value).getKey().toString();
			setText(key);
			setOpaque(true);
				/* put a border on top of first 'out of selection' ite and around selected items */
				if (isSelected)
					setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
				else if ( (index==filterModel.getFilterSize()) && (index>0))
					setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.red));
				else
					setBorder(null);
				/* set foreground/background depending on wether elements are selected  and wether
				 * or not they are in filter
				 */
				if ( (index>=filterModel.getFilterSize()) || (filterModel.getFilterSize()==filterModel.getSize())){
				    if (isSelected){
				    	setBackground(list.getForeground());
					    setForeground(list.getBackground());
				    }
				    else {
				    	setBackground(list.getBackground());
					    setForeground(list.getForeground());
				    }
				} else {
					if (isSelected){
				    	setBackground(list.getSelectionForeground());
				    	setForeground(list.getSelectionBackground());
					} else {
				    	setBackground(list.getSelectionBackground());
				    	setForeground(list.getSelectionForeground());	
					}
				}
				/* set bold to mark selected elements */
				if(isSelected)
					setFont(bold);
				else
					setFont(normal);
			
			return this;
		}
	}
	/** This class is the core junction between the list of element, 
	 * the filter textfield and the rendering of JList component*/
	private class FilteredListModel extends AbstractListModel implements DocumentListener, ActionListener{
				
		private ArrayList fullList=new ArrayList();
		private ArrayList filteredList = new ArrayList();
		private ArrayList rejectedList = new ArrayList();
		private String key;
		/**
		 * This is mainly for convenience with output from map.entrySet.toArray() that 
		 * this method accept an Object[] parameter. The content of array should be Map.Entry 
		 * where key is a String and value is an XMLElement
		 * 
		 * @param list the list of element to filter from in this model
		 */
		public void setFilteredList(Object[] list){
			fullList.clear();
			filteredList.clear();
			for (int i = 0; i<list.length;i++)
				fullList.add((Map.Entry)list[i]);
			filteredList.addAll(fullList);
			if (key!=null)
				filter(key);
		}
		public int getFilterSize(){
			return filteredList.size();
		}
		public int getSize() {
			return fullList.size();
		}
		
		public Object getElementAt(int index) {
			if (index>=0){
				if ( index < filteredList.size()){
					return filteredList.get(index);
				} else if (index < fullList.size()){
					return rejectedList.get(index-filteredList.size());
				}
			}
			return null;
		}
		
		private boolean match(String key, XMLElement element){
			if ((filterDatas!=null) && (filterDatas.length>0) && (element instanceof XMLComplexElement)) {
				XMLComplexElement cmel=(XMLComplexElement) element;
				for (int i=0;i<filterDatas.length;i++){
					XMLElement el = cmel.get(filterDatas[i].toString());
					if (el!=null) {
						String value = el.toValue();
						if ( (value!=null) && (value.toLowerCase().indexOf(key)>=0))
							return true;
					}
				}
			} 
			
			String value = element.toValue();
			if ( (value!=null) && (value.toLowerCase().indexOf(key)>=0))
				return true;
			
			return false;
		}
		private boolean splitAndMatch(String key, XMLElement element){
			key = key.toLowerCase().trim();
			String[] elements = key.split("\\s");
			for (int i = 0;i<elements.length;i++)
				if (!match(elements[i],element))
					return false;
			return (key.length()>0);
		}
		
		/**
		 * updates model according to filter key
		 */
		public void filter(String key){
			this.key=key;
			filteredList.clear();
			rejectedList.clear();
			for (Iterator i = fullList.iterator();i.hasNext();){
				Map.Entry entry = (Map.Entry)i.next();
//				String name= entry.getKey().toString();
				XMLElement element = (XMLElement)entry.getValue();
				if (splitAndMatch(key,element))
					filteredList.add(entry);
				else
					rejectedList.add(entry);
			}
			fireContentsChanged(this, 0, getSize());
		}
		
		/* Manage changes in textfield */
		private void updateFilter(Document doc){
			try {
				String key;
				key = doc.getText(0, doc.getLength());
				filter(key);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}			
		}
		/** Do nothing */
		public void changedUpdate(DocumentEvent e) {
			// nothing to do
		}
		/**
		 * Handles updates in Textfield, requires to redo filtering and updates filter accordingly
		 * @param e DocumentEvent
		 */
		public void insertUpdate(DocumentEvent e) {
			updateFilter(e.getDocument());
		}
		/**
		 * Handles updates in Textfield, requires to redo filtering and updates filter accordingly
		 * @param e DocumentEvent
		 */
		public void removeUpdate(DocumentEvent e) {
			updateFilter(e.getDocument());
		}
		
		/* manage actions in texfield */
		
		/** triggered when action is performed in TextField.
		 * This mean a 'enter' from textfield and we select a specific elt in
		 * JList if there is only one match if filtering
		 */
		public void actionPerformed(ActionEvent e) {
			if (filteredList.size()==1){
				longList.setSelectedIndex(0);
			}
		}
		
	}
}
