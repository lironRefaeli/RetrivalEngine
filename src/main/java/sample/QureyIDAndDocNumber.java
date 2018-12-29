package sample;

/**
 * class that represent an object with a queryID and a document number that retrieved for that query search
 * only for display purposes
 */
public class QureyIDAndDocNumber {

    public String queryID;
    public String docNum;

    public QureyIDAndDocNumber(String queryID, String docNum)
    {
        this.queryID = queryID;
        this.docNum = docNum;
    }

    public String getQueryID()
    {
        return queryID;
    }

    public String getDocNum()
    {
        return docNum;
    }
}
