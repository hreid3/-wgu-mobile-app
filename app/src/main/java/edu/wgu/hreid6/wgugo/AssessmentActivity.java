package edu.wgu.hreid6.wgugo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import edu.wgu.hreid6.wgugo.adapter.AssessmentPhotoListAdapter;
import edu.wgu.hreid6.wgugo.data.model.Assessment;
import edu.wgu.hreid6.wgugo.data.model.Course;
import edu.wgu.hreid6.wgugo.data.model.WguEvent;

import static android.util.Log.e;
import static android.util.Log.i;
import static edu.wgu.hreid6.wgugo.FormHelper.generateKey;
import static edu.wgu.hreid6.wgugo.FormHelper.getDateFromTextView;
import static edu.wgu.hreid6.wgugo.FormHelper.getDisplayDate;
import static edu.wgu.hreid6.wgugo.FormHelper.setListViewHeightBasedOnChildren;

public class AssessmentActivity extends BaseAndroidActivity implements Schedulable, Sharable {

    // This activity will keep more internal state, an antipattern these days for UI development in the functional programming world.
    // Maybe I should tried using scala.
    private ViewGroup viewGroup;
    private Assessment assessment;
    private String photoURI;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assessment = null;
        photoURI = null;
        setContentView(R.layout.activity_assessment);
        this.viewGroup = (ViewGroup) findViewById(R.id.layout_content_assessment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner statuses = (Spinner) findViewById(R.id.fld_assessment_type);
        ArrayAdapter<Assessment.TYPE> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Assessment.TYPE.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statuses.setAdapter(adapter);
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        ((TextView) findViewById(R.id.ro_due_date)).setText(getDisplayDate(year, month, day));
        Integer assessmentId = getIntent().getIntExtra(ASSESSMENT_ID, -1);
        if (!(assessmentId < 0)) {
            try {
                assessment = assessmentDao.getById(assessmentId);
            } catch (SQLException e) {
                e(getLocalClassName(), "no assessment with id " + assessmentId, e);
            }
            if (assessment != null) {
                TextView textView = (TextView) findViewById(R.id.id_assessment);
                textView.setText(assessmentId.toString());
                TextView dueDate = (TextView) findViewById(R.id.ro_due_date);
                TextView assessmentsNotes = (EditText) findViewById(R.id.fld_notes);

                assessmentsNotes.setText(assessment.getNotes() != null ? assessment.getNotes() : "");

                if (assessment.getDueDate() != null) {
                    dueDate.setText(getDisplayDate(assessment.getDueDate()));
                }
                for (int i = 0; i < Assessment.TYPE.values().length; i++) {
                    if (assessment.getType() == Assessment.TYPE.values()[i]) {
                        statuses.setSelection(i, true);
                        break;
                    }
                }
            } else {
                throw new RuntimeException("We should never get here for assessment");
            }
        } else {
            assessment = new Assessment();
        }
        Collection<String> photos = assessment.getFilePaths();
        AssessmentPhotoListAdapter assessmentListAdapter = new AssessmentPhotoListAdapter(this, R.layout.list_assessment_photo_item, new ArrayList<String>(photos), R.layout.list_assessment_photo_item);
        final ListView listView = (ListView) findViewById(R.id.assessment_photos_list);
        listView.setAdapter(assessmentListAdapter);
        setListViewHeightBasedOnChildren(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

    public void doAddPhoto(View v) {
        dispatchTakePictureIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this addsc items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(0, MENU_ITEM_SAVE_ASSESSMENT, 100, R.string.save);
        menu.findItem(MENU_ITEM_SAVE_ASSESSMENT).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        // your code.
        super.onBackPressed();
        Intent intent = new Intent(this, CourseDetailActivity.class);
        Integer i = getIntent().getIntExtra(COURSE_ID, -1);
        intent.putExtra(COURSE_ID, i);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        Intent intent = new Intent(this, CourseDetailActivity.class);
        switch (id) {
            case android.R.id.home:
                Integer i = getIntent().getIntExtra(COURSE_ID, -1);
                intent.putExtra(COURSE_ID, i);
                startActivity(intent);
                return true;
            case MENU_ITEM_SAVE_ASSESSMENT:
                Snackbar.make(getViewGroup(), "Saving...", Snackbar.LENGTH_LONG).setAction("Action", null).show(); // Progress
                // Validate form
                try {
                    // Title cannot be empty
                    // Start Date and End dates cannot be empty
                    // End must not be less than start date
                    // Course mentor cannot be null
                    TextView dueDate = (TextView) viewGroup.findViewById(R.id.ro_due_date);
                    Spinner type = (Spinner) viewGroup.findViewById(R.id.fld_assessment_type);
                    EditText notes = (EditText) viewGroup.findViewById(R.id.fld_notes);

                    if (true) {
//                        Graduate graduate = getGraduate();
                        Course course = courseDao.getById(getIntent().getIntExtra(COURSE_ID, -1));
                        if (course == null) {
                            throw new Exception("Where is the course for " + getIntent().getIntExtra(COURSE_ID, -1));
                        }
                        Assessment assessment = null;
                        TextView anIdFld = (TextView) viewGroup.findViewById(R.id.id_assessment);
                        if (anIdFld != null && anIdFld.getText() != null && anIdFld.getText().toString().length() > 0) {
                            assessment = assessmentDao.getById(Integer.parseInt(anIdFld.getText().toString()));
                        } else {
                            assessment = new Assessment();
                        }
                        assessment.setDueDate(getDateFromTextView(dueDate));
                        if (notes.getText() != null && notes.getText().length() > 0) {
                            assessment.setNotes(notes.getText().toString());
                        }

                        assessment.setType((Assessment.TYPE) type.getSelectedItem());
                        assessment.setCourse(course);

                        final ListView listView = (ListView) findViewById(R.id.assessment_photos_list);
                        AssessmentPhotoListAdapter listAdapter = (AssessmentPhotoListAdapter)listView.getAdapter();

                        assessment.clearPaths();
                        for(int j = 0; j < listAdapter.getCount(); j++) {
                            assessment.addFilePath(listAdapter.getItem(j));
                        }

                        if (assessmentDao.createOrUpdate(assessment)) {
                            i(getLocalClassName(), "create or update for assessment success:  " + course.getTitle());
                            saySomething("Assessment successfully saved.");
                            intent.putExtra(COURSE_ID, course.getId());
                            intent.putExtra(ASSESSMENT_ID, assessment.getId());
                            startActivity(intent);
                        }
                    } else {
                        return false;
                    }
                } catch (Exception ex) {
                    e(getLocalClassName(), "Could not create or update course", ex);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                photoFile.createNewFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            if (!photoFile.exists()) {
                throw new RuntimeException("File does not exist");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                this.photoURI = photoFile.toString();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            final ListView listView = (ListView) findViewById(R.id.assessment_photos_list);
            AssessmentPhotoListAdapter listAdapter = (AssessmentPhotoListAdapter)listView.getAdapter();
            listAdapter.add(photoURI.toString());
            listAdapter.notifyDataSetChanged();
            photoURI = null; //
            setListViewHeightBasedOnChildren((ListView) findViewById(R.id.assessment_photos_list));
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File storageDir = getFilesDir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    public void deleteAssessmentPhotoFromGrid(View v) {
        final ListView listView = (ListView) findViewById(R.id.assessment_photos_list);
        AssessmentPhotoListAdapter listAdapter = (AssessmentPhotoListAdapter)listView.getAdapter();
        Object tag = v.getTag();
        if (tag != null) {
            String path = (String)tag;
            listAdapter.remove(path);
            listAdapter.notifyDataSetChanged();
            setListViewHeightBasedOnChildren(listView);
        }
    }

    @Override
    protected ViewGroup getViewGroup() {
        return viewGroup;
    }

    public boolean isScheduleable() {
        return (getIntent().getIntExtra(ASSESSMENT_ID, -1) > -1); // Only saved courses are schedulable
    }

    @Override
    public WguEvent getWguEvent() {
        try {
            WguEvent event = new WguEvent();
            Spinner type = (Spinner) viewGroup.findViewById(R.id.fld_assessment_type);
            String title = ((Assessment.TYPE) type.getSelectedItem()).toString() + " Assessment";
            event.setTitle(title);

            TextView dueDate = (TextView) viewGroup.findViewById(R.id.ro_due_date);
            event.setStartTime(getDateFromTextView(dueDate).getTime());
            event.setEndTime(getDateFromTextView(dueDate).getTime());

            event.setKey(generateKey(this, getIntent().getIntExtra(ASSESSMENT_ID, -1) > -1));
            event.setEventDescription(title + " Event to remind student is due.");
            event.setNotes(((TextView)viewGroup.findViewById(R.id.fld_notes)).getText().toString());
            return event;
        } catch (Exception ex) {
            String message = "Could not add an event";
            e(getLocalClassName(), message, ex);
            saySomething(message);
        }
        return null;
    }

    @Override
    public String getEventKey() {
        try {
            return generateKey(this, getIntent().getIntExtra(ASSESSMENT_ID, -1) > -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
