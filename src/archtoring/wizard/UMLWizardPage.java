package archtoring.wizard;

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class UMLWizardPage extends WizardPage implements Listener {

	private Text projectName;
	private Composite container;
	private Button selectFileButton;
	private java.net.URI locationURI;
	private Text projectURI;
	
	private boolean hasName = false;
	private boolean hasModel = false;

	public UMLWizardPage() {
		super("Import UML model");
		setTitle("Import UML packagr diagram of architecture");
		setDescription("Select UML file to import");
	}
	
	private boolean isComplete() {
		return hasName && hasModel;
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		Label labelName = new Label(container, SWT.NONE);
		labelName.setText("Project name");

		projectName = new Text(container, SWT.BORDER | SWT.SINGLE);
		projectName.setText("");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		projectName.setLayoutData(gd);
		projectName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (!projectName.getText().isEmpty()) {
					setErrorMessage(null);
					hasName = true;
					setPageComplete(isComplete());
				} else {
					setErrorMessage("You must input a name for the project!");
					hasName = false;
					setPageComplete(false);
				}
			}
		});
		
		new Label(container, SWT.NONE);
		
		Label labelModel = new Label(container, SWT.NONE);
		labelModel.setText("Model location");

		projectURI = new Text(container, SWT.BORDER | SWT.SINGLE);
		projectURI.setText("");
		projectURI.setLayoutData(gd);
		projectURI.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				File umlFile = new File(projectURI.getText());
				if(!umlFile.exists()) {
					setErrorMessage("Could not locate UML file. Try another path.");
					hasModel = false;
					setPageComplete(false);
				} else {
					setErrorMessage(null);
					hasModel = true;
					setPageComplete(isComplete());
				}
			}
		});

		selectFileButton = new Button(container, SWT.PUSH);
		selectFileButton.setText("Browse");
		selectFileButton.addListener(SWT.Selection, this);

		// required to avoid an error in the system
		setControl(container);
		setPageComplete(false);

	}

	public String getProjectName() {
		return projectName.getText();
	}

	public java.net.URI getLocationURI() {
		return locationURI;
	}

	@Override
	public void handleEvent(Event event) {
		if (event.widget == selectFileButton) {
			FileDialog dialog = new FileDialog(UMLWizardPage.this.container.getShell(), SWT.OPEN);
			dialog.setText("Select Model File");
			String[] extensions = { "*.uml" };
			dialog.setFilterExtensions(extensions);
			if (dialog.open() != null) {
				String filename = dialog.getFileName();
				if (filename == "")
					System.out.println("You didn't select any file.");
				else {
					System.out.println("You chose " + dialog.getFilterPath() + "\\" + filename);
					locationURI = new File(dialog.getFilterPath() + "\\" + filename).toURI();
					projectURI.setText(locationURI.getPath());
					setPageComplete(true);
				}
			}
		}
	}
}