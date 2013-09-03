/**
 *
 * Example of how to run a JQL query and return a list of resulting issues
 *
 */

import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.web.bean.PagerFilter

jqlSearch = "project in (DT)"
SearchService searchService = ComponentAccessor.getComponent(SearchService.class)
IssueManager issueManager = ComponentAccessor.getIssueManager()

// Grab the current user or fall back to a default for edits
def UserUtil userUtil = ComponentAccessor.getUserUtil()
def User user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

if (!user) {
    user = userUtil.getUserObject('jenkins')
}

def List<Issue> issues = null

SearchService.ParseResult parseResult =  searchService.parseQuery(user, jqlSearch)
if (parseResult.valid) {
    def searchResult = searchService.search(user, parseResult.getQuery(), PagerFilter.getUnlimitedFilter())
    // Transform issues from DocumentIssueImpl to the "pure" form IssueImpl (some methods don't work with DocumentIssueImps)
    issues = searchResult.issues.collect {issueManager.getIssueObject(it.id)}
} else {
    log.error("Invalid JQL: " + jqlSearch);
}