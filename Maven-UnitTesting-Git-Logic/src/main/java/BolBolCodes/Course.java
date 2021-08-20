package BolBolCodes;

public class Course extends Offering{

    private boolean status;

    public Course(Offering offer){
        super(offer.getCode(), offer.getName() , offer.getInstructor() , offer.getUnits() , offer.getDays(), offer.getTime(),
                offer.getExamTimeStart() , offer.getExamTimeEnd(), offer.getCapacity(), offer.getPrerequisites());

        this.status = false;
    }

    public String getStatus(){
        if(this.status){
            return "finalized";
        }
        return "non-finalized";
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
