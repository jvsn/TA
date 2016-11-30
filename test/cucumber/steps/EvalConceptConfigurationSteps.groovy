package cucumber.steps

import cucumber.api.PendingException
import pages.ConceptPages.AddConceptPage
import pages.ConceptPages.EditConceptPage
import pages.ConceptPages.ShowConceptPage
import pages.EvaluationConceptPages.AddEvaluationConceptPage
import pages.EvaluationConceptPages.EditEvaluationConceptPage
import pages.EvaluationConceptPages.ShowEvaluationConceptPage
import steps.EvalConceptDataAndOperations
import ta.Concept
import ta.EvaluationConcept
import ta.EvaluationConceptController

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)


Given(~/^The evaluation concept is "([^"]*)" with concepts "([^"]*)", "([^"]*)", "([^"]*)"$/) {
            String evaluationConceptName, String c1, String c2, String c3 ->
    List<String> list_concepts = [c1, c2, c3]
    EvalConceptDataAndOperations.createEvalConcept(evaluationConceptName, list_concepts)
    def eval = EvaluationConcept.findByNome(evaluationConceptName)
    assert eval != null
}
When(~/^I update the "([^"]*)" concept to "([^"]*)" concept$/) { String inicial, String proximo ->
    def toUpdate = EvaluationConcept.findByNome(inicial)
    assert toUpdate != null
    toUpdate.setNome(proximo)
    assert toUpdate.nome == proximo
    EvalConceptDataAndOperations.updateEvalConcept(toUpdate)
    def eval = EvaluationConcept.findByNome(proximo)
    assert eval != null
}
Then(~/^The evaluation concept "([^"]*)" is set$/) { String nome ->
    def eval = EvaluationConcept.findByNome(nome)
    assert eval != null
}

When(~/^I update the "([^"]*)" concept to "([^"]*)" evaluation concept with no concepts$/) { String antigo, String novo ->
    def toUpdate = EvaluationConcept.findByNome(antigo)
    assert toUpdate != null
    toUpdate.nome = novo
    toUpdate.conceitos = []
    assert toUpdate.conceitos.empty
    EvalConceptDataAndOperations.updateEvalConcept(toUpdate)
    EvaluationConcept atual = EvaluationConcept.findByNome(antigo)
    assert atual != null
}

Then(~/^The atual concept is "([^"]*)"$/) { String nome ->
    EvaluationConcept eval = EvaluationConcept.findByNome(nome)
    assert eval != null
    assert eval.hasErrors()
}
Given(~/^The "([^"]*)" evaluation concept with concepts "([^"]*)", "([^"]*)", "([^"]*)" is set/)
        { String ini, String c1, String c2, String c3 ->
    Set<String> conceitos = [c1, c2, c3]
    for(conceito in conceitos){
        to AddConceptPage
        at AddConceptPage
        page.fillConceptDetails(conceito)
        page.selectCreate()
    }
    to AddEvaluationConceptPage
    at AddEvaluationConceptPage
    page.fillNameEvaluationConcept(ini)
    page.selectConcepts(conceitos)
    page.createEvalConcept()
}

When(~/^I edit the "([^"]*)" evaluation concept with a invalid number of concepts$/) { String arg1 ->
    at ShowEvaluationConceptPage
    page.editEvalConcept()
    at EditEvaluationConceptPage
    page.editEvalConceptWithoutConcept()
    at ShowEvaluationConceptPage
}

Then(~/^The concepts of "([^"]*)" are the same\.$/) { String entr ->
    assert page.testConcepts(entr.split(", ").sort())
}

When(~/^I edit the "([^"]*)" field to "([^"]*)" evaluation concept$/) { String antigo, String novo ->
    at ShowEvaluationConceptPage
    page.btnEditConcept(antigo)
    at ShowConceptPage
    page.editButton()
    at EditConceptPage
    page.fillConceptName(novo)
    page.btnUpdateConcept()
    at ShowConceptPage
}

Then(~/^I can see the concept "([^"]*)"$/) { String concept ->
    assert page.matchConcept(concept)
}
And(~/^I can't see the concept "([^"]*)"\.$/) { String concept_fail ->
    assert !page.matchConcept(concept_fail)
}
When(~/^I delete the Concept "([^"]*)"$/) { String conceptName ->
    EvalConceptDataAndOperations.deleteConcept(conceptName)
}
Then(~/^The Concept "([^"]*)" isn't deleted$/) { String conceptName ->
    Concept concept = Concept.findByNome(conceptName)
    assert concept != null
}
When(~/^I delete the Evaluation Concept "([^"]*)"$/) { String evaluationConceptName ->
    EvalConceptDataAndOperations.deleteEvaluationConcept(evaluationConceptName)
}
Then(~/^The Evaluation Concept "([^"]*)" is deleted$/) { String evaluationConceptName ->
    EvaluationConcept evaluationConcept = EvaluationConcept.findByNome(evaluationConceptName)
    assert evaluationConcept == null
}
And(~/^the Concept "([^"]*)" is deleted$/) { String conceptName ->
    Concept concept = Concept.findByNome(conceptName)
    assert concept == null
}
