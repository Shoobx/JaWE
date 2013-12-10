package org.enhydra.jawe.components.wfxml;

import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;

public class DefInfos extends XMLCollection {

   public DefInfos () {
      super((XMLComplexElement)null,false);
   }

   public XMLElement generateNewElement() {
      return new DefInfo(this);
   }

}
