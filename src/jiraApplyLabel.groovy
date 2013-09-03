/**
 *
 * Check the summary of a new issue and adds a label to that issue if the
 * pattern is matched.
 *
 */

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.label.LabelManager
import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.util.UserUtil

// For debugging in the script console
def issueManager = ComponentAccessor.getIssueManager()
def thisIssue = issueManager.getIssueObject("DT-1")

// For production
// def MutableIssue thisIssue = issue

def newLabel = 'my_label'
def summaryPattern = "Some Summary Segment"

// Grab the current user or fall back to a default for edits
def UserUtil userUtil = ComponentAccessor.getUserUtil()
def User user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

if (!user) {
    user = userUtil.getUserObject('jenkins')
}

// If the pattern is found in the summary, add the label
if (summaryPattern == thisIssue.getSummary().find(summaryPattern)) {
    def LabelManager labelManager = ComponentAccessor.getComponent(LabelManager)
    def labels = labelManager.getLabels(thisIssue.id).collect{it.getLabel()}
    labels += newLabel
    labelManager.setLabels(user,thisIssue.id,labels.toSet(),false,false)
}