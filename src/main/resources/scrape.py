import csv
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Replace the path to your chromedriver
chrome_options = Options()
driver = webdriver.Chrome(options=chrome_options)

# Replace the URL of the website with the list of nouns
url = 'https://www.talkenglish.com/vocabulary/top-1500-nouns.aspx'
driver.get(url)

# Wait for the table to load
table = WebDriverWait(driver, 10).until(
    EC.presence_of_element_located((By.ID, 'GridView3'))
)

# Find all the rows in the table
rows = table.find_elements(By.TAG_NAME, 'tr')

# Extract the nouns from the table
nouns = []
for row in rows:
    cells = row.find_elements(By.TAG_NAME, 'td')
    noun = cells[1].text.strip()
    nouns.append(noun)

# Save the nouns to a CSV file
with open('nouns.csv', 'w', newline='') as csvfile:
    writer = csv.writer(csvfile)
    for noun in nouns:
        writer.writerow([noun])

# Close the browser
driver.quit()