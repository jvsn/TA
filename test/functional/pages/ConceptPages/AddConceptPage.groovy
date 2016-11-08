package pages.ConceptPages

import geb.Page

/**
 * Created by João Vasconcelos on 07/11/2016.
 */
class AddConceptPage extends Page{
    static url = "/TA/concept/create"

    static at = {
        title ==~ /Criar Concept/
        //title ==~ /Criar Concept/
    }

    def fillConceptDetails(String nome){
        $("form").nome = nome
    }

    def selectCreate(){
        $("input", name: "create").click()
    }

}
