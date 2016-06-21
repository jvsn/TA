package ta

//import org.grails.datastore.mapping.query.Query​

class Student {
    String name;
    String login;
    double average;
    List criteriaAndEvaluations
    static hasMany = [criteriaAndEvaluations:EvaluationsByCriterion]

    static constraints = {
        name blank : false
        login unique : true, blank:false;
    }

    public Student(String name, String login){
        this.name = name;
        this.login = login;
        this.criterionsAndEvaluations = new LinkedList<>();
        def criterions = Criterion.findAll()
        for(int i =0; i < criterions.size();i++){
            EvaluationsByCriterion crit = new EvaluationsByCriterion(criterions.get(i));
            this.criterionsAndEvaluations.add(crit)
        }
    }

    public void calcMedia() {
        int qtdEvaluations = 0
        double tempMedia = 0
        List<Evaluation> evaluationsInCriterion
        for (int i = 0; i < this.criteriaAndEvaluations.size(); i++) {
            evaluationsInCriterion = this.criteriaAndEvaluations[i].getEvaluations()
            for (int j = 0; j < evaluationsInCriterion.size(); j++) {
                String eval = evaluationsInCriterion.get(j).value
                if (!eval.equals("--")) {
                    qtdEvaluations++
                    if (eval.equals("MA")) tempMedia += 9
                    else if (eval.equals("MPA")) tempMedia += 6
                    else tempMedia += 3
                }
            }
        }
        if (qtdEvaluations > 0) {
            tempMedia /= qtdEvaluations
            this.average = tempMedia
        } else {
            this.average = 0
        }
    }

    /*public void addEvaluation(Evaluation evaluationInstance){
        for(int i = 0; i< this.criteriaAndEvaluations.size(); i++){
            if(this.criteriaAndEvaluations.get(i).getCriterion().getDescription().equals(evaluationInstance.criterion.description)){
                this.criteriaAndEvaluations.get(i).addEvaluation(evaluationInstance);
            }
        }
    }*/

    public void addEvaluation(Evaluation evaluationInstance){
        if(this.findEvaluationByCriterion(evaluationInstance.getCriterion().getDescription()) != null) {
            for (int i = 0; i < this.criteriaAndEvaluations.size(); i++) {
                if (this.criteriaAndEvaluations[i].getCriterion().getDescription().equals(evaluationInstance.criterion.description)) {
                    this.criteriaAndEvaluations[i].addEvaluation(evaluationInstance)
                }
            }
        }else {
            EvaluationsByCriterion newEvByCrit = new EvaluationsByCriterion(evaluationInstance.criterion)
            newEvByCrit.addEvaluation(evaluationInstance)
            newEvByCrit.save(flush: true)
            this.addToCriteriaAndEvaluations(newEvByCrit)
        }
        this.calcMedia()
    }


    public void deleteEvaluation(Evaluation evaluationInstance){

        for(int i = 0; i< this.criteriaAndEvaluations.size(); i++){
            if(this.criteriaAndEvaluations[i].getCriterion().getDescription().equals(evaluationInstance.criterion.description)){
                this.criteriaAndEvaluations[i].deleteEvaluation(evaluationInstance);
            }
        }
    }

    public EvaluationsByCriterion findEvaluationByCriterion(String criterionName){
        if(this.criteriaAndEvaluations == null) return null;
        for(int i =0; i<this.criteriaAndEvaluations.size(); i++) {
            if (this.criteriaAndEvaluations[i].getCriterion().getDescription().equals(criterionName)) {
                return this.criteriaAndEvaluations[i];
            }
        }
    }

    public void addEvaluationsByCriterion(EvaluationsByCriterion evCriterion){
        if(!this.findEvaluationByCriterion(evCriterion.getCriterion().getDescription())){
            this.addToCriteriaAndEvaluations(evCriterion);
        }
    }

    public boolean evaluationExist(Evaluation evaluationInstance){

        for(int i = 0; i<this.criteriaAndEvaluations.size(); i++){
            if(this.criteriaAndEvaluations[i].getCriterion().getDescription().equals(evaluationInstance.getCriterion().getDescription())){
                List<Evaluation> evaluationsForThisCriterion = this.criteriaAndEvaluations[i].evaluations;
                for(int j=0; j<evaluationsForThisCriterion.size();j++){
                    if(evaluationsForThisCriterion.compatibleTo(evaluationInstance)){
                        return true
                    }
                }
        }

    }
        return false
    }

    /*private boolean criterionExists(String criterionDescription){
        for(int i=0;i<this.criteriaAndEvaluations.size();i++){
            if(this.criteriaAndEvaluations.get(i).criterion.description.equals(criterionDescription))
        }
    }*/
}