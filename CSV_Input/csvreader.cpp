#include "csvreader.h"

CSVReader::CSVReader(std::ifstream file)
{

}

size_t CSVReader::size() const
{
    return m_data.size();
}

void CSVReader::readNextRow(std::istream &str)
{
    std::string         line;
    std::getline(str, line);

    std::stringstream   lineStream(line);
    std::string         cell;

    m_data.clear();
    while(std::getline(lineStream, cell, ';'))
    {
        m_data.push_back(cell););
    }
}

