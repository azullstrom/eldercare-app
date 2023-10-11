[1mdiff --git a/app/src/main/java/com/example/eldercare/ElderlyOverview.java b/app/src/main/java/com/example/eldercare/ElderlyOverview.java[m
[1mindex 0ef9d42..79c3a09 100644[m
[1m--- a/app/src/main/java/com/example/eldercare/ElderlyOverview.java[m
[1m+++ b/app/src/main/java/com/example/eldercare/ElderlyOverview.java[m
[36m@@ -42,7 +42,7 @@[m [mpublic class ElderlyOverview extends AppCompatActivity {[m
        user= FirebaseAuth.getInstance().getCurrentUser();[m
         if (user != null) {[m
             // User is signed in[m
[31m-[m
[32m+[m[32m            getIntent().getStringExtra("usernameCaregiver");[m
             Toast.makeText(this, "user is known " + user.getEmail() , Toast.LENGTH_SHORT).show();[m
 [m
 [m
