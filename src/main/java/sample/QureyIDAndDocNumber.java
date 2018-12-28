package sample;

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
