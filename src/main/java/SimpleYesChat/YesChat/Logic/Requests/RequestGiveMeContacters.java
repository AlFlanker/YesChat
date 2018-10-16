package SimpleYesChat.YesChat.Logic.Requests;

import SimpleYesChat.YesChat.Logic.Requests.Enums.Filter;

public class RequestGiveMeContacters extends Request {
    private Filter filter;

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public RequestGiveMeContacters() {
    }
}
