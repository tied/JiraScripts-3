/**
 *
 * Check the summary of a new issue and adds a label to that issue if the
 * pattern is matched.
 *
 * 2013.08.24 - Initial script written
 * 2013.09.10 - Altered user logic for JIRA 6.0 compatibility
 *
 */

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.label.LabelManager
import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserUtil

// For debugging in the script console
def issueManager = ComponentAccessor.getIssueManager()
def thisIssue = issueManager.getIssueObject("DT-1")

// For production
// def MutableIssue thisIssue = issue

def newLabel = 'my_label'
def summaryPattern = "Some Summary Segment"

// Grab the current user or fall back to a default for edits
def ApplicationUser appUser = ComponentAccessor.getJiraAuthenticationContext().getUser()
def User user = appUser.getDirectoryUser()

if (!user) {
    def UserUtil userUtil = ComponentAccessor.getUserUtil()
    appUser = userUtil.getUserByName('jenkins')
    user = appUser.getDirectoryUser()
}

// If the pattern is found in the summary, add the label
if (summaryPattern == thisIssue.getSummary().find(summaryPattern)) {
    def LabelManager labelManager = ComponentAccessor.getComponent(LabelManager)
    def labels = labelManager.getLabels(thisIssue.id).collect{it.getLabel()}
    labels += newLabel
    labelManager.setLabels(user,thisIssue.id,labels.toSet(),false,false)
}