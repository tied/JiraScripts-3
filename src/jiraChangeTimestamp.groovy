/**
 *
 * Returns the timestamp from when an issue last entered a given status.
 * This is hacky, but another solution wasn't immediately apparent in the
 * API.
 *
 */

import com.atlassian.jira.component.ComponentAccessor

/* For Logging
import org.apache.log4j.Category

def Category log = Category.getInstance("com.onresolve.jira.groovy.PostFunction")
log.setLevel(org.apache.log4j.Level.DEBUG)
log.debug "debug statements"
*/

/* Enable this for debugging in the admin console
def issueManager = ComponentAccessor.getIssueManager()
def issue = issueManager.getIssueObject("DT-1")
*/

def changeHistoryManager = ComponentAccessor.getChangeHistoryManager()

def changeList = changeHistoryManager.getChangeHistories(issue)
def changeTimestamp = null
def doneStatus = "In Progress"

for (change in changeList) {
    for (thisChange in change.changeItems) {
        if (thisChange["field"] == "status" && thisChange["newstring"] == doneStatus) {
            if (change.timePerformed > changeTimestamp) {
                changeTimestamp = change.timePerformed
            }
        }
    }
}

changeTimestamp