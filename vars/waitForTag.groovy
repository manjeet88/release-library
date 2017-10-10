#!/usr/bin/groovy

def call(Object ctx, String imageStreamName, String tag, int timeOutSecs = 300) {
  ctx.openshift.withCluster() {
    def imageStream = ctx.openshift.selector('is', imageStreamName)
    ctx.timeout(time: timeOutSecs, unit: 'SECONDS') {
      while (!imageStream.exists()) {
        echo "Waiting for ImageStream ${imageStreamName}"
        ctx.sleep 30
      }
      echo "Waiting for ImageStreamTag ${imageStreamName}:${tag}"
      imageStream.watch {
        def tags = it.object().status.tags
        for (t in tags) {
          if (t.tag == tag && t.items != null && t.items.size() > 0) {
            return true
          }
        }
        return false
      }
    }
  }
  return false
}
