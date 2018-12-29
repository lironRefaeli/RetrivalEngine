package sample;

/**
 * class that represent an object with the entity field and that entity grade's field.
 * only for display purposes
 */
public class EntityAndGrade {

    String entity;
    Double grade;

    public EntityAndGrade( String entity, Double grade)
    {
        this.entity = entity;
        this.grade = grade;
    }

    public String getEntity()
    {
        return entity;
    }

    public Double getGrade()
    {
        return grade;
    }
}
